package com.mspoverlay.domain.game;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest {

    @Test
    void getGames_returnsPlatformGames() throws Exception {
        GameService gameService = mock(GameService.class);
        when(gameService.getGames("windows")).thenReturn(List.of(
                new GameResponse(1L, "minecraft", "Minecraft", "windows")
        ));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new GameController(gameService)).build();

        mockMvc.perform(get("/api/games").param("platform", "windows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].displayName").value("Minecraft"));
    }
}
