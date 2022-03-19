package org.pcloud.senselink.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeviceResponse {
    private String ldid;
    private String name;
    private String location;
    private Long status;
    private String direction;
    private String ip;
    private String createTime;
    private String updateTime;
}
