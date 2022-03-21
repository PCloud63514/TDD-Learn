package org.pcloud.admin.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.service.AdminJoinRequest;
import org.pcloud.admin.service.AdminLoginRequest;
import org.pcloud.admin.service.AdminPasswordInitialRequest;
import org.pcloud.admin.service.AdminGetsResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Validated
@RequiredArgsConstructor
@RequestMapping("admin")
@RestController
public class AdminApi {
    private final AdminService adminService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("join")
    public Admin joinAdmin(@RequestBody AdminJoinRequest request) {
        return adminService.joinAdmin(request);
    }

    @GetMapping
    public AdminGetsResponse getAdmins(@RequestParam(name="size", defaultValue = "10") int size,
                                       @RequestParam(name="offset", defaultValue = "0") int offset) {
        return adminService.getAdmins(PageRequest.of(offset, size));
    }

    @GetMapping("duplicate/id/{id}")
    public boolean duplicateIdCheck(@PathVariable(name = "id") String id) {
        return adminService.duplicateIdCheck(id);
    }

    @PatchMapping("init/password")
    public Admin passwordInitial(@RequestBody AdminPasswordInitialRequest request) {
        return adminService.passwordInit(request);
    }

    @PostMapping("login")
    public void login(@RequestBody AdminLoginRequest request, HttpServletResponse response) {
        adminService.login(request, response);
    }
}
