package org.pcloud.admin.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
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
    @CreatedDate
    @Column(name = "create_at", insertable = false, updatable = false, nullable = false)
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

    public void update(String password) {
        this.password = password;
    }
}
