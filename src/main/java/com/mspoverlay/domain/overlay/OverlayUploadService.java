package com.mspoverlay.domain.overlay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.domain.game.Game;
import com.mspoverlay.domain.game.GameRepository;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.platform.PlatformRepository;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.domain.user.UserRepository;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.infrastructure.jsonschema.OverlayJsonSchemaValidator;
import com.mspoverlay.infrastructure.storage.OverlayStorageService;
import com.mspoverlay.infrastructure.storage.StoredOverlayFiles;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

@Service
public class OverlayUploadService {

    private final OverlayRepository overlayRepository;
    private final PlatformRepository platformRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final OverlayJsonSchemaValidator overlayJsonSchemaValidator;
    private final OverlayStorageService overlayStorageService;
    private final ObjectMapper objectMapper;

    public OverlayUploadService(
            OverlayRepository overlayRepository,
            PlatformRepository platformRepository,
            GameRepository gameRepository,
            UserRepository userRepository,
            OverlayJsonSchemaValidator overlayJsonSchemaValidator,
            OverlayStorageService overlayStorageService,
            ObjectMapper objectMapper
    ) {
        this.overlayRepository = overlayRepository;
        this.platformRepository = platformRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.overlayJsonSchemaValidator = overlayJsonSchemaValidator;
        this.overlayStorageService = overlayStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OverlayUploadResponse upload(Long authenticatedUserId, OverlayUploadRequest request) {
        if (overlayRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.OVERLAY_CODE_DUPLICATED);
        }

        User authorUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Platform platform = platformRepository.findBySlug(request.getPlatform())
                .orElseThrow(() -> new BusinessException(ErrorCode.PLATFORM_NOT_FOUND));
        Game game = resolveGame(request.getGameId(), platform);

        String overlayJson = readOverlayJson(request);
        overlayJsonSchemaValidator.validate(overlayJson);

        OverlayJsonMetadata metadata = extractMetadata(overlayJson);
        validatePlatformConsistency(request.getPlatform(), metadata.platform());

        if (overlayRepository.findByOverlayId(metadata.overlayId()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "overlayId가 이미 존재합니다.");
        }

        StoredOverlayFiles storedFiles = null;
        try {
            storedFiles = overlayStorageService.save(metadata.overlayId(), request.getOverlayJson(), request.getThumbnail());
            Overlay overlay = overlayRepository.save(new Overlay(
                    metadata.overlayId(),
                    request.getCode(),
                    request.getName(),
                    request.getDescription(),
                    platform,
                    game,
                    authorUser,
                    metadata.schemaVersion(),
                    metadata.canvasBaseWidth(),
                    metadata.canvasBaseHeight(),
                    metadata.opacity(),
                    storedFiles.jsonPath(),
                    storedFiles.thumbnailPath()
            ));

            return new OverlayUploadResponse(
                    overlay.getId(),
                    overlay.getOverlayId(),
                    overlay.getCode(),
                    overlay.getJsonPath(),
                    overlay.getThumbnailPath()
            );
        } catch (BusinessException exception) {
            if (storedFiles != null) {
                overlayStorageService.deleteDirectory(storedFiles.overlayDirectory());
            }
            throw exception;
        } catch (RuntimeException exception) {
            if (storedFiles != null) {
                overlayStorageService.deleteDirectory(storedFiles.overlayDirectory());
            }
            throw exception;
        }
    }

    private Game resolveGame(Long gameId, Platform platform) {
        if (gameId == null) {
            return null;
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
        if (!game.getPlatform().getId().equals(platform.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "gameId가 platform과 일치하지 않습니다.");
        }
        return game;
    }

    private String readOverlayJson(OverlayUploadRequest request) {
        try {
            return new String(request.getOverlayJson().getBytes());
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.INVALID_MULTIPART_REQUEST, "overlayJson 파일을 읽을 수 없습니다.");
        }
    }

    private OverlayJsonMetadata extractMetadata(String overlayJson) {
        try {
            JsonNode root = objectMapper.readTree(overlayJson);
            return new OverlayJsonMetadata(
                    root.path("overlayId").asText(),
                    root.path("schemaVersion").asText(),
                    root.path("platform").asText(),
                    root.path("canvas").path("baseWidth").asInt(),
                    root.path("canvas").path("baseHeight").asInt(),
                    root.path("overlaySettings").path("opacity").decimalValue()
            );
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "overlay.json 메타데이터를 읽을 수 없습니다.");
        }
    }

    private void validatePlatformConsistency(String requestPlatform, String jsonPlatform) {
        if (!requestPlatform.equals(jsonPlatform)) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "요청 platform과 overlay.json platform이 일치하지 않습니다.");
        }
    }

    private record OverlayJsonMetadata(
            String overlayId,
            String schemaVersion,
            String platform,
            Integer canvasBaseWidth,
            Integer canvasBaseHeight,
            BigDecimal opacity
    ) {
    }
}
