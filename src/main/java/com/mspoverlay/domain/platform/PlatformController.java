package com.mspoverlay.domain.platform;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;

@RestController
@RequestMapping("/api/platforms")
@Tag(name = "Platform", description = "Platform lookup APIs")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    @Operation(summary = "Get active platform list")
    public ApiResponse<List<PlatformResponse>> getPlatforms() {
        return ApiResponse.ok(platformService.getPlatforms());
    }
}
