package org.pcloud.admin.repository;

import org.pcloud.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findAdminByIdAndPassword(String id, String password);
}
