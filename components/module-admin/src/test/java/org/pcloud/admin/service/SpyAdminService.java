package org.pcloud.admin.service;

import org.pcloud.admin.domain.Admin;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletResponse;

public class SpyAdminService implements AdminService {
    public AdminJoinResponse joinAdmin_returnValue;
    public AdminJoinRequest joinAdmin_argumentRequest;
    public AdminGetsResponse getAdmins_returnValue;
    public PageRequest getAdmins_argumentRequest;
    public String duplicateIdCheck_argumentId;
    public boolean duplicateIdCheck_returnValue;
    public AdminPasswordInitialRequest passwordInit_argumentRequest;
    public Admin passwordInit_returnValue;
    public AdminLoginRequest login_argumentRequest;
    public HttpServletResponse login_argumentHttpServletResponse;

    @Override
    public AdminJoinResponse joinAdmin(AdminJoinRequest request) {
        this.joinAdmin_argumentRequest = request;
        return joinAdmin_returnValue;
    }

    @Override
    public AdminGetsResponse getAdmins(PageRequest pageRequest) {
        this.getAdmins_argumentRequest = pageRequest;
        return getAdmins_returnValue;
    }

    @Override
    public boolean duplicateIdCheck(String id) {
        this.duplicateIdCheck_argumentId = id;
        return duplicateIdCheck_returnValue;
    }

    @Override
    public Admin passwordInit(AdminPasswordInitialRequest request) {
        this.passwordInit_argumentRequest = request;
        return passwordInit_returnValue;
    }

    @Override
    public void login(AdminLoginRequest request, HttpServletResponse response) {
        this.login_argumentRequest = request;
        this.login_argumentHttpServletResponse = response;
    }
}
