package org.pcloud.admin.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class AdminGetsResponse {
    private int total;
    private List<AdminSearchResponse> list;
}
