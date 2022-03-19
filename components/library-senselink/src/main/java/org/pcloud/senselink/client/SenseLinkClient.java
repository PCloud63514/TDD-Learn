package org.pcloud.senselink.client;

import org.pcloud.senselink.data.request.RecognitionQualityRequest;
import org.pcloud.senselink.data.response.DeviceResponse;
import org.pcloud.senselink.data.response.GetDevicesResponse;
import org.pcloud.senselink.data.response.RecognitionQualityResponse;
import org.pcloud.senselink.data.response.SenseLinkResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sense-link", url = "${sense-link.url}")
public interface SenseLinkClient {

    @PostMapping
    SenseLinkResponse<RecognitionQualityResponse> recognitionQuality(@RequestBody RecognitionQualityRequest request);

    @GetMapping("/devices")
    SenseLinkResponse<GetDevicesResponse> getDevices();

    @GetMapping("/devices/{ldid}")
    SenseLinkResponse<DeviceResponse> getDevice(@PathVariable(name = "ldid") String ldid);
}
