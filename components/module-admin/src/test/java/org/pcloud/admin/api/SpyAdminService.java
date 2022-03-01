package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;

public class SpyAdminService implements AdminService {
    public Admin joinAdmin_returnValue;
    public AdminJoinRequest joinAdmin_argumentRequest;

    @Override
    public Admin joinAdmin(AdminJoinRequest request) {
        this.joinAdmin_argumentRequest = request;
        return joinAdmin_returnValue;
    }
}
