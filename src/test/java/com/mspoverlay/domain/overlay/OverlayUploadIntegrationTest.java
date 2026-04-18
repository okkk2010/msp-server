package com.mspoverlay.domain.overlay;

import com.mspoverlay.domain.user.OAuthProvider;
import com.mspoverlay.domain.user.User;
import com.mspoverlay.domain.user.UserRepository;
import com.mspoverlay.global.security.AuthenticatedUser;
import com.mspoverlay.global.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
class OverlayUploadIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("msp_overlay")
            .withUsername("msp_user")
            .withPassword("msp_password");

    private static final Path STORAGE_ROOT = createStorageRoot();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("app.storage.base-path", () -> STORAGE_ROOT.toString());
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OverlayRepository overlayRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private User user;
    private String accessToken;

    @BeforeEach
    void setUp() throws IOException {
        overlayRepository.deleteAll();
        userRepository.deleteAll();
        deleteStorageRootContents();

        user = userRepository.save(new User(
                OAuthProvider.GOOGLE,
                "google-user-1",
                "user@example.com",
                "테스트 사용자",
                null
        ));
        accessToken = jwtTokenProvider.createAccessToken(
                new AuthenticatedUser(user.getId(), user.getEmail(), user.getName())
        );
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @AfterAll
    static void tearDown() throws IOException {
        if (Files.exists(STORAGE_ROOT)) {
            try (var paths = Files.walk(STORAGE_ROOT)) {
                paths.sorted((left, right) -> right.compareTo(left))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        }
    }

    @Test
    void uploadOverlay_savesOverlayAndFiles() throws Exception {
        MockMultipartFile overlayJson = new MockMultipartFile(
                "overlayJson",
                "overlay.json",
                "application/json",
                validOverlayJson("ovl_upload_001").getBytes()
        );
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumbnail.png",
                "image/png",
                pngBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/overlays")
                        .file(overlayJson)
                        .file(thumbnail)
                        .param("name", "업로드 테스트")
                        .param("description", "설명")
                        .param("platform", "windows")
                        .param("code", "ABC123")
                        .header(AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.overlayId").value("ovl_upload_001"))
                .andExpect(jsonPath("$.data.jsonPath").value("/storage/overlays/ovl_upload_001/overlay.json"))
                .andExpect(jsonPath("$.data.thumbnailPath").value("/storage/overlays/ovl_upload_001/thumbnail.png"));

        Overlay savedOverlay = overlayRepository.findByCode("ABC123").orElseThrow();
        assertThat(savedOverlay.getAuthorUser().getId()).isEqualTo(user.getId());
        assertThat(savedOverlay.getJsonPath()).isEqualTo("/storage/overlays/ovl_upload_001/overlay.json");
        assertThat(savedOverlay.getThumbnailPath()).isEqualTo("/storage/overlays/ovl_upload_001/thumbnail.png");
        assertThat(Files.exists(STORAGE_ROOT.resolve("overlays/ovl_upload_001/overlay.json"))).isTrue();
        assertThat(Files.exists(STORAGE_ROOT.resolve("overlays/ovl_upload_001/thumbnail.png"))).isTrue();
    }

    @Test
    void uploadOverlay_returnsInvalidOverlayJsonWhenSchemaValidationFails() throws Exception {
        MockMultipartFile overlayJson = new MockMultipartFile(
                "overlayJson",
                "overlay.json",
                "application/json",
                invalidOverlayJsonMissingMeta("ovl_upload_002").getBytes()
        );
        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail",
                "thumbnail.png",
                "image/png",
                pngBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/overlays")
                        .file(overlayJson)
                        .file(thumbnail)
                        .param("name", "업로드 테스트")
                        .param("platform", "windows")
                        .param("code", "DEF456")
                        .header(AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INVALID_OVERLAY_JSON"));

        assertThat(overlayRepository.findByCode("DEF456")).isEmpty();
        assertThat(Files.exists(STORAGE_ROOT.resolve("overlays/ovl_upload_002"))).isFalse();
    }

    private static Path createStorageRoot() {
        try {
            return Files.createTempDirectory("msp-overlay-upload-test");
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private void deleteStorageRootContents() throws IOException {
        if (Files.notExists(STORAGE_ROOT)) {
            Files.createDirectories(STORAGE_ROOT);
            return;
        }

        try (var paths = Files.walk(STORAGE_ROOT)) {
            paths.filter(path -> !path.equals(STORAGE_ROOT))
                    .sorted((left, right) -> right.compareTo(left))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    private byte[] pngBytes() {
        return Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9WnSUs8AAAAASUVORK5CYII="
        );
    }

    private String validOverlayJson(String overlayId) {
        return """
                {
                  "schemaVersion": "1.0.0",
                  "overlayId": "%s",
                  "name": "Manual Upload Sample",
                  "platform": "windows",
                  "canvas": {
                    "baseWidth": 1920,
                    "baseHeight": 1080
                  },
                  "overlaySettings": {
                    "opacity": 0.85
                  },
                  "elements": [
                    {
                      "id": "rect-1",
                      "type": "rect",
                      "x": 120,
                      "y": 80,
                      "width": 400,
                      "height": 240,
                      "rotation": 0,
                      "opacity": 0.7,
                      "zIndex": 1,
                      "visible": true,
                      "locked": false,
                      "fillColor": "#000000",
                      "strokeColor": "#ffffff",
                      "strokeWidth": 2,
                      "cornerRadius": 12
                    }
                  ],
                  "meta": {
                    "createdAt": "2026-04-15T19:30:00+09:00",
                    "updatedAt": "2026-04-15T19:45:00+09:00"
                  }
                }
                """.formatted(overlayId);
    }

    private String invalidOverlayJsonMissingMeta(String overlayId) {
        return """
                {
                  "schemaVersion": "1.0.0",
                  "overlayId": "%s",
                  "name": "Invalid Upload Sample",
                  "platform": "windows",
                  "canvas": {
                    "baseWidth": 1920,
                    "baseHeight": 1080
                  },
                  "overlaySettings": {
                    "opacity": 0.85
                  },
                  "elements": []
                }
                """.formatted(overlayId);
    }
}
