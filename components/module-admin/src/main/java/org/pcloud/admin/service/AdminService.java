package org.pcloud.admin.service;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface AdminService {
    Admin joinAdmin(AdminJoinRequest request);

    List<AdminSearchResponse> getAdmins(PageRequest pageRequest);

    boolean duplicateIdCheck(String id);
}
