package com.mspoverlay.domain.overlay;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.global.response.PageResponse;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.global.exception.GlobalExceptionHandler;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
class OverlayControllerTest {

    @Test
    void getOverlays_returnsPagedOverlayList() throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        when(overlayQueryService.getOverlays(0, 20, "front", "windows", null, null, "newest"))
                .thenReturn(PageResponse.from(new PageImpl<>(
                        List.of(new OverlaySummaryResponse(
                                1L,
                                "ovl_front_dashboard_001",
                                "FD0001",
                                "Frontend Dashboard Sample",
                                "Temporary seed data",
                                "windows",
                                "minecraft",
                                "/storage/overlays/ovl_front_dashboard_001/thumbnail.png",
                                "Front Demo User",
                                OffsetDateTime.parse("2026-04-19T12:00:00+09:00"),
                                OffsetDateTime.parse("2026-04-19T12:10:00+09:00")
                        )),
                        PageRequest.of(0, 20),
                        1
                )));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).build();

        mockMvc.perform(get("/api/overlays")
                        .param("page", "0")
                        .param("size", "20")
                        .param("keyword", "front")
                        .param("platform", "windows")
                        .param("sort", "newest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].overlayId").value("ovl_front_dashboard_001"))
                .andExpect(jsonPath("$.data.content[0].platform").value("windows"));
    }

    @Test
    void getOverlayDetail_returnsOverlayDetail() throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        when(overlayQueryService.getOverlayDetail(eq("ovl_front_dashboard_001")))
                .thenReturn(new OverlayDetailResponse(
                        1L,
                        "ovl_front_dashboard_001",
                        "FD0001",
                        "Frontend Dashboard Sample",
                        "Temporary seed data",
                        "windows",
                        "minecraft",
                        "1.0.0",
                        1920,
                        1080,
                        new BigDecimal("0.92"),
                        "/storage/overlays/ovl_front_dashboard_001/overlay.json",
                        "/storage/overlays/ovl_front_dashboard_001/thumbnail.png",
                        new OverlayAuthorResponse(1L, "Front Demo User", "front-demo@example.com"),
                        OffsetDateTime.parse("2026-04-19T12:00:00+09:00"),
                        OffsetDateTime.parse("2026-04-19T12:10:00+09:00")
                ));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).build();

        mockMvc.perform(get("/api/overlays/ovl_front_dashboard_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.overlayId").value("ovl_front_dashboard_001"))
                .andExpect(jsonPath("$.data.author.email").value("front-demo@example.com"));
    }

    @Test
    void getOverlayByCode_returnsOverlayJsonPayload() throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        when(overlayQueryService.getOverlayByCode(eq("abc123")))
                .thenReturn(new OverlayCodeLoadResponse(
                        1L,
                        "ovl_front_dashboard_001",
                        "ABC123",
                        "Frontend Dashboard Sample",
                        "Temporary seed data",
                        new OverlayCodeLoadPlatformResponse(1L, "Windows", "windows"),
                        new OverlayCodeLoadGameResponse(10L, "minecraft", "Minecraft"),
                        "/storage/overlays/ovl_front_dashboard_001/thumbnail.png",
                        "1.0.0",
                        objectMapper.readTree("""
                                {
                                  "schemaVersion": "1.0.0",
                                  "overlayId": "ovl_front_dashboard_001",
                                  "name": "Frontend Dashboard Sample"
                                }
                                """),
                        OffsetDateTime.parse("2026-04-19T12:00:00+09:00"),
                        OffsetDateTime.parse("2026-04-19T12:10:00+09:00")
                ));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).build();

        mockMvc.perform(get("/api/overlays/code/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("ABC123"))
                .andExpect(jsonPath("$.data.platform.slug").value("windows"))
                .andExpect(jsonPath("$.data.game.displayName").value("Minecraft"))
                .andExpect(jsonPath("$.data.overlayJson.overlayId").value("ovl_front_dashboard_001"));
    }

    @Test
    void getOverlayByCode_returnsBadRequestWhenCodeIsInvalid() throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        when(overlayQueryService.getOverlayByCode(eq("ab-12")))
                .thenThrow(new BusinessException(ErrorCode.INVALID_OVERLAY_CODE));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).setControllerAdvice(new GlobalExceptionHandler()).build();

        mockMvc.perform(get("/api/overlays/code/ab-12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INVALID_OVERLAY_CODE"));
    }

    @Test
    void getOverlays_withoutFilters_returnsDefaultPage() throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        when(overlayQueryService.getOverlays(null, null, null, null, null, null, null))
                .thenReturn(PageResponse.from(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0)));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).build();

        mockMvc.perform(get("/api/overlays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getOverlayByCode_returnsInternalServerErrorAndLogsException(CapturedOutput output) throws Exception {
        OverlayUploadService overlayUploadService = mock(OverlayUploadService.class);
        OverlayQueryService overlayQueryService = mock(OverlayQueryService.class);
        OverlayCommandService overlayCommandService = mock(OverlayCommandService.class);
        when(overlayQueryService.getOverlayByCode(eq("abc123")))
                .thenThrow(new RuntimeException("overlay query crashed"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(
                new OverlayController(overlayUploadService, overlayQueryService, overlayCommandService)
        ).setControllerAdvice(new GlobalExceptionHandler()).build();

        mockMvc.perform(get("/api/overlays/code/abc123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));

        assertThat(output.getOut())
                .contains("Unhandled exception on GET /api/overlays/code/abc123")
                .contains("overlay query crashed");
    }
}
