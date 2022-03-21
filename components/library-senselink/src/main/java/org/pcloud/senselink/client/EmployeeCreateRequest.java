package org.pcloud.senselink.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmployeeCreateRequest {
    private int type;
    private String name;
    private String avatarBytes;
    private int gender;
    private String birthday;
    private Long departmentId;
    private String icNumber;

    public EmployeeCreateRequest(int type, String name, String avatarBytes, int gender, String birthday, Long departmentId, String icNumber) {
        this.type = type;
        this.name = name;
        this.avatarBytes = avatarBytes;
        this.gender = gender;
        this.birthday = birthday;
        this.departmentId = departmentId;
        this.icNumber = icNumber;
    }
}
