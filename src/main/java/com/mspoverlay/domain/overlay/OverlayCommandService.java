package com.mspoverlay.domain.overlay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.domain.game.Game;
import com.mspoverlay.domain.game.GameRepository;
import com.mspoverlay.domain.library.UserLibraryRepository;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.platform.PlatformRepository;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.infrastructure.jsonschema.OverlayJsonSchemaValidator;
import com.mspoverlay.infrastructure.storage.OverlayStorageService;
import com.mspoverlay.infrastructure.storage.StoredOverlayFiles;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OverlayCommandService {

    private final OverlayRepository overlayRepository;
    private final PlatformRepository platformRepository;
    private final GameRepository gameRepository;
    private final UserLibraryRepository userLibraryRepository;
    private final OverlayJsonSchemaValidator overlayJsonSchemaValidator;
    private final OverlayStorageService overlayStorageService;
    private final ObjectMapper objectMapper;

    public OverlayCommandService(
            OverlayRepository overlayRepository,
            PlatformRepository platformRepository,
            GameRepository gameRepository,
            UserLibraryRepository userLibraryRepository,
            OverlayJsonSchemaValidator overlayJsonSchemaValidator,
            OverlayStorageService overlayStorageService,
            ObjectMapper objectMapper
    ) {
        this.overlayRepository = overlayRepository;
        this.platformRepository = platformRepository;
        this.gameRepository = gameRepository;
        this.userLibraryRepository = userLibraryRepository;
        this.overlayJsonSchemaValidator = overlayJsonSchemaValidator;
        this.overlayStorageService = overlayStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OverlayUploadResponse update(String overlayId, Long authenticatedUserId, OverlayUpdateRequest request) {
        Overlay overlay = overlayRepository.findWithDetailsByOverlayId(overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND));
        validateAuthor(overlay, authenticatedUserId);

        String targetName = request.getName() != null ? request.getName() : overlay.getName();
        String targetDescription = request.getDescription() != null ? normalizeDescription(request.getDescription()) : overlay.getDescription();
        String targetCode = request.getCode() != null ? request.getCode().toUpperCase() : overlay.getCode();
        if (overlayRepository.existsByCodeAndIdNot(targetCode, overlay.getId())) {
            throw new BusinessException(ErrorCode.OVERLAY_CODE_DUPLICATED);
        }

        boolean overlayJsonProvided = hasFile(request.getOverlayJson());
        String requestedPlatform = request.getPlatform() != null ? request.getPlatform() : overlay.getPlatform().getSlug();
        if (!overlayJsonProvided && !requestedPlatform.equals(overlay.getPlatform().getSlug())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "platform 변경 시 overlayJson 파일이 필요합니다.");
        }

        OverlayJsonMetadata metadata = overlayJsonProvided
                ? validateAndExtractMetadata(overlayId, request.getOverlayJson(), requestedPlatform)
                : OverlayJsonMetadata.from(overlay);

        Platform targetPlatform = platformRepository.findBySlug(requestedPlatform)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLATFORM_NOT_FOUND));
        Game targetGame = resolveGame(request.getGameId(), targetPlatform, request.getGameId() != null);
        if (request.getGameId() == null && request.getPlatform() == null) {
            targetGame = overlay.getGame();
        }

        StoredOverlayFiles storedOverlayFiles = overlayStorageService.replace(
                overlay.getOverlayId(),
                request.getOverlayJson(),
                request.getThumbnail(),
                overlay.getJsonPath(),
                overlay.getThumbnailPath()
        );

        overlay.update(
                targetName,
                targetDescription,
                targetPlatform,
                targetGame,
                targetCode,
                metadata.schemaVersion(),
                metadata.canvasBaseWidth(),
                metadata.canvasBaseHeight(),
                metadata.opacity(),
                storedOverlayFiles.jsonPath(),
                storedOverlayFiles.thumbnailPath()
        );

        return new OverlayUploadResponse(
                overlay.getId(),
                overlay.getOverlayId(),
                overlay.getCode(),
                overlay.getJsonPath(),
                overlay.getThumbnailPath()
        );
    }

    @Transactional
    public void delete(String overlayId, Long authenticatedUserId) {
        Overlay overlay = overlayRepository.findByOverlayId(overlayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OVERLAY_NOT_FOUND));
        validateAuthor(overlay, authenticatedUserId);

        userLibraryRepository.deleteAllByOverlayId(overlay.getId());
        overlayRepository.delete(overlay);
        overlayStorageService.deleteOverlayDirectory(overlay.getOverlayId());
    }

    private void validateAuthor(Overlay overlay, Long authenticatedUserId) {
        if (!overlay.getAuthorUser().getId().equals(authenticatedUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private OverlayJsonMetadata validateAndExtractMetadata(String overlayId, MultipartFile overlayJson, String requestPlatform) {
        String overlayJsonContent = readOverlayJson(overlayJson);
        overlayJsonSchemaValidator.validate(overlayJsonContent);
        OverlayJsonMetadata metadata = extractMetadata(overlayJsonContent);
        if (!overlayId.equals(metadata.overlayId())) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "overlayId는 변경할 수 없습니다.");
        }
        if (!requestPlatform.equals(metadata.platform())) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "요청 platform과 overlay.json platform이 일치하지 않습니다.");
        }
        return metadata;
    }

    private Game resolveGame(Long gameId, Platform platform, boolean explicitUpdate) {
        if (gameId == null) {
            return explicitUpdate ? null : null;
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));
        if (!game.getPlatform().getId().equals(platform.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "gameId가 platform과 일치하지 않습니다.");
        }
        return game;
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private String readOverlayJson(MultipartFile overlayJson) {
        try {
            return new String(overlayJson.getBytes());
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

    private String normalizeDescription(String description) {
        return description != null && description.isBlank() ? null : description;
    }

    private record OverlayJsonMetadata(
            String overlayId,
            String schemaVersion,
            String platform,
            Integer canvasBaseWidth,
            Integer canvasBaseHeight,
            BigDecimal opacity
    ) {
        static OverlayJsonMetadata from(Overlay overlay) {
            return new OverlayJsonMetadata(
                    overlay.getOverlayId(),
                    overlay.getSchemaVersion(),
                    overlay.getPlatform().getSlug(),
                    overlay.getCanvasBaseWidth(),
                    overlay.getCanvasBaseHeight(),
                    overlay.getOpacity()
            );
        }
    }
}
