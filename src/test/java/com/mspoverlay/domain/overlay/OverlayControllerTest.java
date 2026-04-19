package com.mspoverlay.domain.overlay;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.mspoverlay.global.response.PageResponse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
