package com.mspoverlay.domain.game;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game", description = "Game lookup APIs")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    @Operation(summary = "Get active games by platform")
    public ApiResponse<List<GameResponse>> getGames(@RequestParam("platform") String platform) {
        return ApiResponse.ok(gameService.getGames(platform));
    }
}
