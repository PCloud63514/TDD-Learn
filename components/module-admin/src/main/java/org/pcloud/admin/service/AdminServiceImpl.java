package org.pcloud.admin.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.domain.AdminRepository;
import org.pcloud.admin.provider.InitializedPasswordProvider;
import org.pcloud.admin.provider.LocalDateTimeProvider;
import org.pcloud.gateway.network.TokenIssueRequest;
import org.pcloud.gateway.network.JwtTokenResponse;
import org.pcloud.gateway.network.AuthClient;
import org.pcloud.gateway.utils.JwtAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final InitializedPasswordProvider initializedPasswordProvider;
    private final AdminRepository adminRepository;
    private final AuthClient authClient;

    @Override
    public AdminJoinResponse joinAdmin(AdminJoinRequest request) {
        adminRepository.findById(request.getId()).ifPresent(admin -> {
            throw new RuntimeException("중복된 아이디가 존재합니다.");
        });
        Admin admin = Admin.create(request.getId(), request.getPassword(), "ADMIN", "Default", localDateTimeProvider.now());
        Admin save = adminRepository.save(admin);
        return new AdminJoinResponse(save.getId(), save.getRole(), save.getStatus(), save.isNeedChangePassword(), save.getCreateAt());
    }

    @Override
    public AdminGetsResponse getAdmins(PageRequest pageRequest) {
        long total = adminRepository.count();
        Page<Admin> findAllResponse = adminRepository.findAll(pageRequest);

        List<AdminSearchResponse> list = findAllResponse.stream()
                .map(r -> new AdminSearchResponse(r.getId(), r.getRole(), r.getStatus(), r.isNeedChangePassword(), r.getCreateAt()))
                .collect(Collectors.toList());
        return new AdminGetsResponse((int) total, list);
    }

    @Override
    public boolean duplicateIdCheck(String id) {
        return adminRepository.existsById(id);
    }

    @Override
    public Admin passwordInit(AdminPasswordInitialRequest request) {
        Admin admin = adminRepository.findById(request.getId())
                .orElseThrow(RuntimeException::new);

        admin.update(initializedPasswordProvider.initializedPassword());

        return admin;
    }

    @Override
    public void login(AdminLoginRequest request, HttpServletResponse response) {
        Admin admin = adminRepository.findAdminByIdAndPassword(request.getId(), request.getPassword())
                .orElseThrow(RuntimeException::new);
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        TokenIssueRequest tokenIssueRequest = new TokenIssueRequest("admin", "ROLE_" + admin.getRole(), data, 60000, 600000);

        JwtTokenResponse tokenResponse = authClient.issueToken(tokenIssueRequest);
        JwtAuthUtil.injectAuthorization(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), response);
    }
}
