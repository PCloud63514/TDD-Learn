package org.pcloud.senselink.data.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetDevicesResponse {
    private List<DeviceResponse> resultList;
}
