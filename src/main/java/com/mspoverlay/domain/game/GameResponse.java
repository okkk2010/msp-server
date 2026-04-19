package com.mspoverlay.domain.game;

public record GameResponse(
        Long id,
        String slug,
        String displayName,
        String platform
) {

    public static GameResponse from(Game game) {
        return new GameResponse(
                game.getId(),
                game.getSlug(),
                game.getDisplayName(),
                game.getPlatform().getSlug()
        );
    }
}
