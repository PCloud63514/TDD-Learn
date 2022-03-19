package org.pcloud.senselink.data.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionQualityRequest {
    @NotBlank(message = "RecognitionQualityRequest.faceAvatar argument is null")
    private String faceAvatar;
}

