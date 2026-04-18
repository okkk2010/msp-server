package com.mspoverlay.infrastructure.jsonschema;

import com.mspoverlay.global.config.JacksonConfig;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverlayJsonSchemaValidatorTest {

    private OverlayJsonSchemaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OverlayJsonSchemaValidator(
                new JacksonConfig().objectMapper(),
                new ClassPathResource("jsonschema/overlay-schema.json")
        );
    }

    @Test
    void validate_acceptsValidOverlayJson() {
        assertDoesNotThrow(() -> validator.validate(validOverlayJson()));
    }

    @Test
    void validate_rejectsMissingRequiredField() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validate(validOverlayJson().replace("\"meta\": {", "\"removedMeta\": {"))
        );

        assertEquals(ErrorCode.INVALID_OVERLAY_JSON, exception.getErrorCode());
        assertTrue(exception.getMessage() != null && !exception.getMessage().isBlank());
    }

    @Test
    void validate_rejectsUnsupportedPlatform() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validate(validOverlayJson().replace("\"platform\": \"windows\"", "\"platform\": \"ios\""))
        );

        assertEquals(ErrorCode.INVALID_OVERLAY_JSON, exception.getErrorCode());
        assertTrue(exception.getMessage() != null && !exception.getMessage().isBlank());
    }

    @Test
    void validate_rejectsOpacityOutOfRange() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validate(validOverlayJson().replace("\"opacity\": 0.85", "\"opacity\": 1.25"))
        );

        assertEquals(ErrorCode.INVALID_OVERLAY_JSON, exception.getErrorCode());
        assertTrue(exception.getMessage() != null && !exception.getMessage().isBlank());
    }

    @Test
    void validate_rejectsUnsupportedElementType() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> validator.validate(validOverlayJson().replace("\"type\": \"rect\"", "\"type\": \"image\""))
        );

        assertEquals(ErrorCode.INVALID_OVERLAY_JSON, exception.getErrorCode());
        assertTrue(exception.getMessage() != null && !exception.getMessage().isBlank());
    }

    private String validOverlayJson() {
        return """
                {
                  "schemaVersion": "1.0.0",
                  "overlayId": "ovl_001",
                  "name": "Minecraft Center Focus",
                  "platform": "windows",
                  "game": {
                    "id": "minecraft",
                    "name": "Minecraft"
                  },
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
                    },
                    {
                      "id": "line-1",
                      "type": "line",
                      "x1": 0,
                      "y1": 0,
                      "x2": 1920,
                      "y2": 1080,
                      "opacity": 0.9,
                      "zIndex": 2,
                      "visible": true,
                      "locked": false,
                      "strokeColor": "#ff0000",
                      "strokeWidth": 4,
                      "dashStyle": "solid"
                    }
                  ],
                  "meta": {
                    "createdAt": "2026-04-15T19:30:00+09:00",
                    "updatedAt": "2026-04-15T19:45:00+09:00"
                  }
                }
                """;
    }
}
