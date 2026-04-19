package com.mspoverlay.domain.game;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameResponse> getGames(String platformSlug) {
        if (platformSlug == null || platformSlug.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "platform은 필수입니다.");
        }

        return gameRepository.findAllByPlatformSlugAndActiveTrueOrderByDisplayNameAsc(platformSlug)
                .stream()
                .map(GameResponse::from)
                .toList();
    }
}
