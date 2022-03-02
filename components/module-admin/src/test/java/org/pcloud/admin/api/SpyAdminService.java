package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class SpyAdminService implements AdminService {
    public Admin joinAdmin_returnValue;
    public AdminJoinRequest joinAdmin_argumentRequest;
    public List<AdminSearchResponse> getAdmins_returnValue;
    public PageRequest getAdmins_argumentRequest;
    public String duplicateIdCheck_argumentId;
    public boolean duplicateIdCheck_returnValue;

    @Override
    public Admin joinAdmin(AdminJoinRequest request) {
        this.joinAdmin_argumentRequest = request;
        return joinAdmin_returnValue;
    }

    @Override
    public List<AdminSearchResponse> getAdmins(PageRequest pageRequest) {
        this.getAdmins_argumentRequest = pageRequest;
        return getAdmins_returnValue;
    }

    @Override
    public boolean duplicateIdCheck(String id) {
        this.duplicateIdCheck_argumentId = id;
        return duplicateIdCheck_returnValue;
    }
}
