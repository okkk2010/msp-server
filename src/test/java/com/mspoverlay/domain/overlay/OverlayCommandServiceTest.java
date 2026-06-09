package com.mspoverlay.domain.overlay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.domain.game.Game;
import com.mspoverlay.domain.game.GameRepository;
import com.mspoverlay.domain.like.OverlayLikeRepository;
import com.mspoverlay.domain.library.UserLibraryRepository;
import com.mspoverlay.domain.platform.Platform;
import com.mspoverlay.domain.platform.PlatformRepository;
import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.mspoverlay.infrastructure.jsonschema.OverlayJsonSchemaValidator;
import com.mspoverlay.infrastructure.storage.OverlayStorageService;
import com.mspoverlay.infrastructure.storage.StoredOverlayFiles;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OverlayCommandServiceTest {

    @Test
    void update_rejectsRequestFromNonAuthor() {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        OverlayCommandService service = new OverlayCommandService(
                overlayRepository,
                mock(PlatformRepository.class),
                mock(GameRepository.class),
                mock(UserLibraryRepository.class),
                mock(OverlayLikeRepository.class),
                mock(OverlayJsonSchemaValidator.class),
                mock(OverlayStorageService.class),
                new ObjectMapper()
        );
        when(overlayRepository.findWithDetailsByOverlayId("ovl_001")).thenReturn(Optional.of(overlay(1L, "ABC123")));

        assertThatThrownBy(() -> service.update("ovl_001", 99L, new OverlayUpdateRequest()))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void update_updatesMetadataAndFilesForAuthor() {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        PlatformRepository platformRepository = mock(PlatformRepository.class);
        GameRepository gameRepository = mock(GameRepository.class);
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayJsonSchemaValidator validator = mock(OverlayJsonSchemaValidator.class);
        OverlayStorageService storageService = mock(OverlayStorageService.class);
        Overlay overlay = overlay(1L, "ABC123");
        Platform windows = new Platform("Windows", "windows", true);
        setId(windows, "id", 1L);
        Game minecraft = new Game(windows, "minecraft", "Minecraft", true);

        when(overlayRepository.findWithDetailsByOverlayId("ovl_001")).thenReturn(Optional.of(overlay));
        when(overlayRepository.existsByCodeAndIdNot("ZXCVBN", 1L)).thenReturn(false);
        when(platformRepository.findBySlug("windows")).thenReturn(Optional.of(windows));
        when(gameRepository.findById(10L)).thenReturn(Optional.of(minecraft));
        when(storageService.replace(anyString(), any(), any(), anyString(), anyString()))
                .thenReturn(new StoredOverlayFiles(
                        "/storage/overlays/ovl_001/overlay.json",
                        "/storage/overlays/ovl_001/thumbnail.png",
                        Path.of("storage/overlays/ovl_001"),
                        Path.of("storage/overlays/ovl_001/overlay.json"),
                        Path.of("storage/overlays/ovl_001/thumbnail.png")
                ));

        OverlayCommandService service = new OverlayCommandService(
                overlayRepository,
                platformRepository,
                gameRepository,
                userLibraryRepository,
                mock(OverlayLikeRepository.class),
                validator,
                storageService,
                new ObjectMapper()
        );

        OverlayUpdateRequest request = new OverlayUpdateRequest();
        request.setName("Updated Overlay");
        request.setDescription("updated description");
        request.setPlatform("windows");
        request.setGameId(10L);
        request.setCode("ZXCVBN");
        request.setOverlayJson(new MockMultipartFile(
                "overlayJson",
                "overlay.json",
                "application/json",
                validOverlayJson("ovl_001", "windows", "1.0.1", 0.75).getBytes()
        ));
        request.setThumbnail(new MockMultipartFile(
                "thumbnail",
                "thumbnail.png",
                "image/png",
                new byte[] {1, 2, 3}
        ));

        OverlayUploadResponse response = service.update("ovl_001", 1L, request);

        assertThat(response.overlayId()).isEqualTo("ovl_001");
        assertThat(response.code()).isEqualTo("ZXCVBN");
        assertThat(overlay.getName()).isEqualTo("Updated Overlay");
        assertThat(overlay.getCode()).isEqualTo("ZXCVBN");
        assertThat(overlay.getSchemaVersion()).isEqualTo("1.0.1");
        assertThat(overlay.getOpacity()).isEqualTo(new BigDecimal("0.75"));
        verify(validator).validate(anyString());
    }

    @Test
    void delete_removesOverlayLibraryMappingsAndStorage() {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayStorageService storageService = mock(OverlayStorageService.class);
        Overlay overlay = overlay(7L, "QWERTY");
        when(overlayRepository.findByOverlayId("ovl_001")).thenReturn(Optional.of(overlay));

        OverlayCommandService service = new OverlayCommandService(
                overlayRepository,
                mock(PlatformRepository.class),
                mock(GameRepository.class),
                userLibraryRepository,
                mock(OverlayLikeRepository.class),
                mock(OverlayJsonSchemaValidator.class),
                storageService,
                new ObjectMapper()
        );

        service.delete("ovl_001", 1L);

        verify(userLibraryRepository).deleteAllByOverlayId(7L);
        verify(overlayRepository).delete(overlay);
        verify(storageService).deleteOverlayDirectory("ovl_001");
    }

    @Test
    void delete_rejectsRequestFromNonAuthor() {
        OverlayRepository overlayRepository = mock(OverlayRepository.class);
        UserLibraryRepository userLibraryRepository = mock(UserLibraryRepository.class);
        OverlayStorageService storageService = mock(OverlayStorageService.class);
        when(overlayRepository.findByOverlayId("ovl_001")).thenReturn(Optional.of(overlay(7L, "QWERTY")));

        OverlayCommandService service = new OverlayCommandService(
                overlayRepository,
                mock(PlatformRepository.class),
                mock(GameRepository.class),
                userLibraryRepository,
                mock(OverlayLikeRepository.class),
                mock(OverlayJsonSchemaValidator.class),
                storageService,
                new ObjectMapper()
        );

        assertThatThrownBy(() -> service.delete("ovl_001", 2L))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(userLibraryRepository, never()).deleteAllByOverlayId(any());
        verify(storageService, never()).deleteOverlayDirectory(anyString());
    }

    private Overlay overlay(Long id, String code) {
        User user = new User(OAuthProvider.GOOGLE, "google-user-1", "user@example.com", "Author", null);
        Platform platform = new Platform("Windows", "windows", true);
        Overlay overlay = new Overlay(
                "ovl_001",
                code,
                "Original Overlay",
                "original description",
                platform,
                null,
                user,
                "1.0.0",
                1920,
                1080,
                new BigDecimal("0.85"),
                "/storage/overlays/ovl_001/overlay.json",
                "/storage/overlays/ovl_001/thumbnail.png"
        );
        setId(user, "id", 1L);
        setId(platform, "id", 1L);
        setId(overlay, "id", id);
        return overlay;
    }

    private void setId(Object target, String fieldName, Long id) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String validOverlayJson(String overlayId, String platform, String schemaVersion, double opacity) {
        return """
                {
                  "schemaVersion": "%s",
                  "overlayId": "%s",
                  "name": "Updated Overlay",
                  "platform": "%s",
                  "canvas": {
                    "baseWidth": 1920,
                    "baseHeight": 1080
                  },
                  "overlaySettings": {
                    "opacity": %s
                  },
                  "elements": [],
                  "meta": {
                    "createdAt": "2026-04-15T19:30:00+09:00",
                    "updatedAt": "2026-04-15T19:45:00+09:00"
                  }
                }
                """.formatted(schemaVersion, overlayId, platform, opacity);
    }
}
