package org.pcloud.admin.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.admin.LocalDateTimeProvider;
import org.pcloud.admin.StubLocalDateTimeProvider;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AdminServiceImplTest {
    AdminServiceImpl adminService;
    StubLocalDateTimeProvider stubLocalDateTimeProvider;
    SpyAdminRepository spyAdminRepository;

    @BeforeEach
    void setUp() {
        spyAdminRepository = new SpyAdminRepository();
        stubLocalDateTimeProvider = new StubLocalDateTimeProvider();
        adminService = new AdminServiceImpl(stubLocalDateTimeProvider, spyAdminRepository);
    }

    @Test
    void joinAdmin_returnAdmin() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();
        AdminJoinRequest givenAdminJoinRequest = new AdminJoinRequest("id", "password");

        Admin admin = adminService.joinAdmin(givenAdminJoinRequest);

        assertThat(admin.getId()).isEqualTo(givenAdminJoinRequest.getId());
        assertThat(admin.getPassword()).isEqualTo(givenAdminJoinRequest.getPassword());
        assertThat(admin.getRole()).isEqualTo("ADMIN");
        assertThat(admin.getStatus()).isEqualTo("Default");
        assertThat(admin.getCreateAt()).isEqualTo(stubLocalDateTimeProvider.now());
    }

    @Test
    void joinAdmin_passesAdminToRepository() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();
        AdminJoinRequest givenAdminJoinRequest = new AdminJoinRequest("id", "password");

        adminService.joinAdmin(givenAdminJoinRequest);

        assertThat(spyAdminRepository.save_argumentAdmin.getId()).isEqualTo(givenAdminJoinRequest.getId());
        assertThat(spyAdminRepository.save_argumentAdmin.getPassword()).isEqualTo(givenAdminJoinRequest.getPassword());
        assertThat(spyAdminRepository.save_argumentAdmin.getRole()).isEqualTo("ADMIN");
        assertThat(spyAdminRepository.save_argumentAdmin.getStatus()).isEqualTo("Default");
        assertThat(spyAdminRepository.save_argumentAdmin.getCreateAt()).isEqualTo(stubLocalDateTimeProvider.now());
    }

    @Test
    void getAdmins_returnAdminSearchResponses() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();

        List<AdminSearchResponse> admins = adminService.getAdmins(null);

        assertThat(admins).isNotEmpty();
        assertThat(admins.get(0).getId()).isEqualTo("id");
        assertThat(admins.get(0).getRole()).isEqualTo("ADMIN");
        assertThat(admins.get(0).getStatus()).isEqualTo("Default");
        assertThat(admins.get(0).isNeedChangePassword()).isEqualTo(false);
        assertThat(admins.get(0).getCreateAt()).isEqualTo(stubLocalDateTimeProvider.now());
    }

    @Test
    void getAdmins_passesPageRequestToRepository() {
        Integer givenOffset = 0;
        Integer givenPageSize = 10;
        PageRequest givenPageRequest = PageRequest.of(givenOffset, givenPageSize);

        adminService.getAdmins(givenPageRequest);

        assertThat(spyAdminRepository.findAll_argumentRequest.getOffset()).isEqualTo(givenOffset.longValue());
        assertThat(spyAdminRepository.findAll_argumentRequest.getPageSize()).isEqualTo(givenPageSize);
    }
}