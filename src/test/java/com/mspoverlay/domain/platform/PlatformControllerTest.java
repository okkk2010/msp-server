package com.mspoverlay.domain.platform;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlatformControllerTest {

    @Test
    void getPlatforms_returnsPlatformList() throws Exception {
        PlatformService platformService = mock(PlatformService.class);
        when(platformService.getPlatforms()).thenReturn(List.of(
                new PlatformResponse(1L, "Windows", "windows"),
                new PlatformResponse(2L, "Android", "android")
        ));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PlatformController(platformService)).build();

        mockMvc.perform(get("/api/platforms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].slug").value("windows"))
                .andExpect(jsonPath("$.data[1].slug").value("android"));
    }
}
