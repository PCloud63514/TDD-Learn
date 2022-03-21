package org.pcloud.admin.service;

import org.pcloud.admin.domain.Admin;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletResponse;

public interface AdminService {
    Admin joinAdmin(AdminJoinRequest request);

    AdminGetsResponse getAdmins(PageRequest pageRequest);

    boolean duplicateIdCheck(String id);

    Admin passwordInit(AdminPasswordInitialRequest request);

    void login(AdminLoginRequest request, HttpServletResponse response);
}
