package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<AdminSearchResponse> getAdmins() {
        return List.of(new AdminSearchResponse("id", "ADMIN",
                "Default", true, LocalDateTime.of(2022, 2, 22, 20, 20, 20)));
    }
}
