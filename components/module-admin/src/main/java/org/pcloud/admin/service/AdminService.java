package org.pcloud.admin.service;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.domain.Admin;

public interface AdminService {
    Admin joinAdmin(AdminJoinRequest request);
}
