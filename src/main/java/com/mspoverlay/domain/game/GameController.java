package com.mspoverlay.domain.game;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mspoverlay.global.response.ApiResponse;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ApiResponse<List<GameResponse>> getGames(@RequestParam("platform") String platform) {
        return ApiResponse.ok(gameService.getGames(platform));
    }
}
