package com.mspoverlay.domain.overlay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.domain.game.Game;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.global.config.StorageProperties;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.infrastructure.storage.OverlayStorageService;

class OverlayQueryServiceTest {

    @Test
    void getOverlayByCode_normalizesCodeAndReturnsOverlayJson() throws IOException {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        Path storageRoot = Files.createTempDirectory("overlay-code-load-test");
        OverlayStorageService overlayStorageService = new OverlayStorageService(new StorageProperties(storageRoot.toString()));
        ObjectMapper objectMapper = new ObjectMapper();
        OverlayQueryService service = new OverlayQueryService(overlayRepository, overlayStorageService, objectMapper);

        Files.createDirectories(storageRoot.resolve("overlays/ovl_001"));
        Files.writeString(
                storageRoot.resolve("overlays/ovl_001/overlay.json"),
                """
                        {
                          "schemaVersion": "1.0.0",
                          "overlayId": "ovl_001",
                          "name": "Minecraft Center Focus"
                        }
                        """,
                StandardCharsets.UTF_8
        );

        Overlay overlay = createOverlay();
        when(overlayRepository.findWithDetailsByCode("ABC123")).thenReturn(java.util.Optional.of(overlay));

        OverlayCodeLoadResponse response = service.getOverlayByCode(" abc123 ");

        assertThat(response.code()).isEqualTo("ABC123");
        assertThat(response.platform().slug()).isEqualTo("windows");
        assertThat(response.game().displayName()).isEqualTo("Minecraft");
        assertThat(response.overlayJson().get("overlayId").asText()).isEqualTo("ovl_001");
    }

    @Test
    void getOverlayByCode_rejectsInvalidCodeFormat() {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        OverlayStorageService overlayStorageService = new OverlayStorageService(new StorageProperties("storage"));
        OverlayQueryService service = new OverlayQueryService(overlayRepository, overlayStorageService, new ObjectMapper());

        assertThatThrownBy(() -> service.getOverlayByCode("ab-12"))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_OVERLAY_CODE);
    }

    private Overlay createOverlay() {
        Platform platform = new Platform("Windows", "windows", true);
        Game game = new Game(platform, "minecraft", "Minecraft", true);
        User author = new User(OAuthProvider.GOOGLE, "google-1", "user@example.com", "User", null);

        return new Overlay(
                "ovl_001",
                "ABC123",
                "Minecraft Center Focus",
                "중앙 시야 고정용 오버레이",
                platform,
                game,
                author,
                "1.0.0",
                1920,
                1080,
                new BigDecimal("0.85"),
                "/storage/overlays/ovl_001/overlay.json",
                "/storage/overlays/ovl_001/thumbnail.png"
        );
    }
}
