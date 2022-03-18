package org.pcloud.smartpass.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("departments")
@RestController
public class DepartmentApi {
    // 생성
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public void createDepartment() {

    }
    // 수정
    // 조회
    // 목록 조회
    // 삭제
}
