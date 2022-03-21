package org.pcloud.senselink.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecognitionQualityResponse {
    private int qaCode;
    private String qaDesc;
    private String qaMsg;
}
