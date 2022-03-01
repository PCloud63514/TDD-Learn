package org.pcloud.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
public class Admin {
    private String id;
    private String password;
    private String role;
    private String status;
    private boolean needChangePassword;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    public Admin(String id, String password, String role, String status, boolean needChangePassword, LocalDateTime createAt) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.status = status;
        this.needChangePassword = needChangePassword;
        this.createAt = createAt;
    }
}
