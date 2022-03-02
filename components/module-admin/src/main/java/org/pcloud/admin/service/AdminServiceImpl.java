package org.pcloud.admin.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.LocalDateTimeProvider;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.request.AdminPasswordInitialRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final AdminRepository adminRepository;

    @Override
    public Admin joinAdmin(AdminJoinRequest request) {
        Admin admin = Admin.create(request.getId(), request.getPassword(), "ADMIN", "Default", localDateTimeProvider.now());
        return adminRepository.save(admin);
    }

    @Override
    public List<AdminSearchResponse> getAdmins(PageRequest pageRequest) {
        Page<Admin> findAllResponse = adminRepository.findAll(pageRequest);

        return findAllResponse.stream()
                .map(r -> new AdminSearchResponse(r.getId(), r.getRole(), r.getStatus(), r.isNeedChangePassword(), r.getCreateAt()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean duplicateIdCheck(String id) {
        return adminRepository.existsById(id);
    }

    @Override
    public Admin passwordInit(AdminPasswordInitialRequest request) {
        adminRepository.findById(request.getId()).orElseThrow(RuntimeException::new);
        return Admin.builder()
                .id("id")
                .password("password")
                .role("ADMIN")
                .status("Default")
                .needChangePassword(false)
                .createAt(localDateTimeProvider.now())
                .build();
    }
}
