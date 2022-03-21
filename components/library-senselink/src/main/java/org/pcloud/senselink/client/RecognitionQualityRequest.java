package org.pcloud.senselink.client;

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

