package org.pcloud.admin.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.LocalDateTimeProvider;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final LocalDateTimeProvider localDateTimeProvider;
    private final AdminRepository adminRepository;

    @Override
    public Admin joinAdmin(AdminJoinRequest request) {
        Admin admin = Admin.builder()
                .id(request.getId())
                .password(request.getPassword())
                .role("ADMIN")
                .status("Default")
                .createAt(localDateTimeProvider.now())
                .build();

        return adminRepository.save(admin);
    }

    @Override
    public List<AdminSearchResponse> getAdmins(PageRequest pageRequest) {
        return null;
    }
}
