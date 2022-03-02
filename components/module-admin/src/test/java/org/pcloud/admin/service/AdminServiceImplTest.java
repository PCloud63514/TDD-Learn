package org.pcloud.admin.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.admin.LocalDateTimeProvider;
import org.pcloud.admin.StubLocalDateTimeProvider;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;

import java.time.LocalDateTime;

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
}