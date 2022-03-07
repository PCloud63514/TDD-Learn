package org.pcloud.admin.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.data.request.AdminLoginRequest;
import org.pcloud.admin.data.response.AdminGetsResponse;
import org.pcloud.admin.provider.InitializedPasswordProvider;
import org.pcloud.admin.provider.LocalDateTimeProvider;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.request.AdminPasswordInitialRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final InitializedPasswordProvider initializedPasswordProvider;
    private final AdminRepository adminRepository;

    @Override
    public Admin joinAdmin(AdminJoinRequest request) {
        Admin admin = Admin.create(request.getId(), request.getPassword(), "ADMIN", "Default", localDateTimeProvider.now());
        return adminRepository.save(admin);
    }

    @Override
    public AdminGetsResponse getAdmins(PageRequest pageRequest) {
        long total = adminRepository.count();
        Page<Admin> findAllResponse = adminRepository.findAll(pageRequest);

        List<AdminSearchResponse> list = findAllResponse.stream()
                .map(r -> new AdminSearchResponse(r.getId(), r.getRole(), r.getStatus(), r.isNeedChangePassword(), r.getCreateAt()))
                .collect(Collectors.toList());
        return new AdminGetsResponse((int)total, list);
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

    }
}
