package com.mspoverlay.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check APIs")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check API health")
    public ApiResponse<HealthResponse> getHealth() {
        return ApiResponse.ok(new HealthResponse("ok"));
    }
}
