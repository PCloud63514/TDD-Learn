package org.pcloud.admin.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

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
    public List<AdminSearchResponse> getAdmins(@RequestParam(name="size", defaultValue = "10") int size,
                                               @RequestParam(name="offset", defaultValue = "0") int offset) {
        return adminService.getAdmins(PageRequest.of(offset, size));
    }
}
