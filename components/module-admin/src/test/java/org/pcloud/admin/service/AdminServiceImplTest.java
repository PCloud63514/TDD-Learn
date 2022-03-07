package org.pcloud.admin.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.request.AdminLoginRequest;
import org.pcloud.admin.data.request.AdminPasswordInitialRequest;
import org.pcloud.admin.data.response.AdminGetsResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.provider.StubInitializedPasswordProvider;
import org.pcloud.admin.provider.StubLocalDateTimeProvider;
import org.pcloud.security.data.response.TokenResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminServiceImplTest {
    AdminServiceImpl adminService;
    StubLocalDateTimeProvider stubLocalDateTimeProvider;
    StubInitializedPasswordProvider stubInitializedPasswordProvider;
    SpyAdminRepository spyAdminRepository;
    SpyAuthClient spyAuthClient;

    @BeforeEach
    void setUp() {
        spyAuthClient = new SpyAuthClient();
        spyAdminRepository = new SpyAdminRepository();
        stubLocalDateTimeProvider = new StubLocalDateTimeProvider();
        stubInitializedPasswordProvider = new StubInitializedPasswordProvider();
        adminService = new AdminServiceImpl(stubLocalDateTimeProvider, stubInitializedPasswordProvider, spyAdminRepository, spyAuthClient);
    }

    @Test
    void joinAdmin_returnAdmin() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();
        AdminJoinRequest givenAdminJoinRequest = new AdminJoinRequest("id", "password");
        spyAdminRepository.findById_returnValue = Optional.empty();

        Admin admin = adminService.joinAdmin(givenAdminJoinRequest);

        assertThat(spyAdminRepository.findById_argumentId).isEqualTo("id");
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
    void joinAdmin_throwRunTimeException() {
        AdminJoinRequest givenAdminJoinRequest = new AdminJoinRequest("id", "password");

        Assertions.assertThrows(RuntimeException.class, ()-> {
            spyAdminRepository.findById_returnValue = Optional.of(Admin.builder().build());
            adminService.joinAdmin(givenAdminJoinRequest);
        });
    }

    @Test
    void getAdmins_returnAdminSearchResponses() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();
        int givenOffset = 0;
        int givenPageSize = 10;
        PageRequest givenPageRequest = PageRequest.of(givenOffset, givenPageSize);
        spyAdminRepository.findAll_returnValue = List.of(Admin.create("id1", "password2", "ADMIN", "Default", stubLocalDateTimeProvider.now()));
        AdminGetsResponse admins = adminService.getAdmins(givenPageRequest);

        assertThat(admins.getList()).isNotEmpty();
        assertThat(admins.getTotal()).isEqualTo(1);
        assertThat(admins.getList().get(0).getId()).isEqualTo("id1");
        assertThat(admins.getList().get(0).getRole()).isEqualTo("ADMIN");
        assertThat(admins.getList().get(0).getStatus()).isEqualTo("Default");
        assertThat(admins.getList().get(0).getCreateAt()).isEqualTo(stubLocalDateTimeProvider.now());
    }

    @Test
    void getAdmins_passesPageRequestToRepository() {
        int givenOffset = 0;
        int givenPageSize = 10;
        PageRequest givenPageRequest = PageRequest.of(givenOffset, givenPageSize);

        adminService.getAdmins(givenPageRequest);

        assertThat(spyAdminRepository.findAll_argumentRequest.getOffset()).isEqualTo(givenOffset);
        assertThat(spyAdminRepository.findAll_argumentRequest.getPageSize()).isEqualTo(givenPageSize);
    }

    @Test
    void duplicateIdCheck_returnValue() {
        boolean id = adminService.duplicateIdCheck("id");

        spyAdminRepository.existsById_returnValue = false;

        assertThat(spyAdminRepository.existsById_returnValue).isEqualTo(id);

    }

    @Test
    void duplicateIdCheck_passesIdToRepository() {
        adminService.duplicateIdCheck("id");

        assertThat(spyAdminRepository.existsById_argumentId).isEqualTo("id");
    }

    @Test
    void passwordInit_returnAdmin() {
        stubLocalDateTimeProvider.now_returnValue = LocalDateTime.now();
        String givenId = "id";
        String givenInitPassword = "initPassword";
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest(givenId);
        String givenRole = "ADMIN";
        String givenStatus = "Default";
        spyAdminRepository.findById_returnValue = Optional.of(Admin.builder()
                .id(givenId)
                .password("password")
                .role(givenRole)
                .status(givenStatus)
                .needChangePassword(false)
                .createAt(stubLocalDateTimeProvider.now())
                .build());

        stubInitializedPasswordProvider.initializedPassword_returnValue = givenInitPassword;

        Admin admin = adminService.passwordInit(givenRequest);

        assertThat(admin.getId()).isEqualTo(givenId);
        assertThat(admin.getPassword()).isEqualTo(stubInitializedPasswordProvider.initializedPassword());
        assertThat(admin.getRole()).isEqualTo(givenRole);
        assertThat(admin.getStatus()).isEqualTo(givenStatus);
        assertThat(admin.isNeedChangePassword()).isEqualTo(false);
        assertThat(admin.getCreateAt()).isEqualTo(stubLocalDateTimeProvider.now());
        assertThat(spyAdminRepository.findById_returnValue.get().getPassword()).isEqualTo(stubInitializedPasswordProvider.initializedPassword());
    }

    @Test
    void passwordInit_passesIdToRepository() {
        String givenId = "id";
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest(givenId);
        spyAdminRepository.findById_returnValue = Optional.of(Admin.builder()
                .id(givenId)
                .password("password")
                .role("ADMIN")
                .status("Default")
                .needChangePassword(false)
                .createAt(stubLocalDateTimeProvider.now())
                .build());

        adminService.passwordInit(givenRequest);

        assertThat(spyAdminRepository.findById_argumentId).isEqualTo(givenId);
    }

    @Test
    void passwordInit_notExistsException() {
        String givenId = "id";
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest(givenId);
        spyAdminRepository.findById_returnValue = Optional.empty();

        assertThrows(RuntimeException.class, () -> {
            adminService.passwordInit(givenRequest);
        });
    }

    @Test
    void login_passesIdAndPasswordToAdminRepository_findAdminIdAndPassword() {
        String givenId = "id";
        String givenPassword = "password";
        AdminLoginRequest givenRequest = new AdminLoginRequest(givenId, givenPassword);
        HttpServletResponse givenResponse = new MockHttpServletResponse();
        Admin givenAdmin = Admin.create(givenId, givenPassword, "role", null, null);
        spyAdminRepository.findAdminByIdAndPassword_returnValue = Optional.of(givenAdmin);
        spyAuthClient.issueToken_returnValue = new TokenResponse("token");

        adminService.login(givenRequest, givenResponse);

        assertThat(spyAdminRepository.findAdminByIdAndPassword_argumentId).isEqualTo(givenId);
        assertThat(spyAdminRepository.findAdminByIdAndPassword_argumentPassword).isEqualTo(givenPassword);
    }

    @Test
    void login_passesTokenIssueRequestToAuthClient_issueToken() {
        String givenId = "id";
        String givenPassword = "password";
        String givenRole = "role";
        AdminLoginRequest givenRequest = new AdminLoginRequest(givenId, givenPassword);
        HttpServletResponse givenResponse = new MockHttpServletResponse();
        Admin givenAdmin = Admin.create(givenId, givenPassword, givenRole, null, null);
        spyAdminRepository.findAdminByIdAndPassword_returnValue = Optional.of(givenAdmin);
        spyAuthClient.issueToken_returnValue = new TokenResponse();

        adminService.login(givenRequest, givenResponse);

        assertThat(spyAuthClient.issueToken_argumentRequest.getIssueRequestDomain()).isEqualTo("admin");
        assertThat(spyAuthClient.issueToken_argumentRequest.getRole()).isEqualTo(givenRole);
    }

    @Test
    void login_returnHttpServletResponse() {
        String givenId = "id";
        String givenPassword = "password";
        String givenRole = "role";
        String givenToken = "token";
        AdminLoginRequest givenRequest = new AdminLoginRequest(givenId, givenPassword);
        HttpServletResponse givenResponse = new MockHttpServletResponse();
        Admin givenAdmin = Admin.create(givenId, givenPassword, givenRole, null, null);
        spyAdminRepository.findAdminByIdAndPassword_returnValue = Optional.of(givenAdmin);
        spyAuthClient.issueToken_returnValue = new TokenResponse(givenToken);

        adminService.login(givenRequest, givenResponse);

        assertThat(givenResponse.getHeader("token")).isEqualTo(givenToken);
    }

    @Test
    void login_throwRunTimeException() {
        String givenId = "id";
        String givenPassword = "password";
        AdminLoginRequest givenRequest = new AdminLoginRequest(givenId, givenPassword);
        HttpServletResponse givenResponse = new MockHttpServletResponse();

        assertThrows(RuntimeException.class, ()-> {
            spyAdminRepository.findAdminByIdAndPassword_returnValue = Optional.empty();
           adminService.login(givenRequest, givenResponse);
        });
    }
}