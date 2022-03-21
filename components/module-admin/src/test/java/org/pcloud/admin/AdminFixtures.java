package org.pcloud.admin;

import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.domain.Admin.AdminBuilder;

import java.time.LocalDateTime;

public class AdminFixtures {
    public static AdminBuilder anAdmin() {
        return Admin.builder()
                .id("id")
                .password("password")
                .role("role")
                .status("status")
                .needChangePassword(true)
                .createAt(LocalDateTime.of(2022, 2, 2,2,2))
                ;
    }
}
