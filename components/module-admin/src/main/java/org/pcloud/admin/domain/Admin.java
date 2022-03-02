package org.pcloud.admin.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Admin {
    @Id
    private String id;
    private String password;
    private String role;
    private String status;
    private boolean needChangePassword;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @Builder
    private Admin(String id, String password, String role, String status, boolean needChangePassword, LocalDateTime createAt) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.status = status;
        this.needChangePassword = needChangePassword;
        this.createAt = createAt;
    }

    public static Admin create(String id, String password, String role, String status, LocalDateTime createAt) {
        return Admin.builder()
                .id(id)
                .password(password)
                .role(role)
                .status(status)
                .needChangePassword(false)
                .createAt(createAt)
                .build();
    }
}
