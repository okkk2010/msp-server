package com.mspoverlay.domain.overlay;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class OverlayUpdateRequest {

    @Size(min = 1, message = "이름은 비어 있을 수 없습니다.")
    private String name;

    private String description;

    @Size(min = 1, message = "플랫폼은 비어 있을 수 없습니다.")
    private String platform;

    private Long gameId;

    @Size(min = 6, max = 6, message = "코드는 6자리여야 합니다.")
    private String code;

    private MultipartFile overlayJson;

    private MultipartFile thumbnail;
}
