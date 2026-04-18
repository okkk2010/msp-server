package com.mspoverlay.domain.overlay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class OverlayUploadRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    private String description;

    @NotBlank(message = "플랫폼은 필수입니다.")
    private String platform;

    private Long gameId;

    @NotBlank(message = "코드는 필수입니다.")
    @Size(min = 6, max = 6, message = "코드는 6자리여야 합니다.")
    private String code;

    @NotNull(message = "overlayJson 파일은 필수입니다.")
    private MultipartFile overlayJson;

    @NotNull(message = "thumbnail 파일은 필수입니다.")
    private MultipartFile thumbnail;
}
