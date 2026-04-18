package com.mspoverlay.infrastructure.jsonschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OverlayJsonSchemaValidator {

    private static final String SCHEMA_LOCATION = "jsonschema/overlay-schema.json";

    private final ObjectMapper objectMapper;
    private final JsonSchema schema;

    @Autowired
    public OverlayJsonSchemaValidator(ObjectMapper objectMapper) {
        this(objectMapper, new ClassPathResource(SCHEMA_LOCATION));
    }

    OverlayJsonSchemaValidator(ObjectMapper objectMapper, Resource schemaResource) {
        this.objectMapper = objectMapper;
        this.schema = loadSchema(schemaResource);
    }

    public void validate(String overlayJson) {
        JsonNode overlayJsonNode = readJson(overlayJson);
        Set<ValidationMessage> validationMessages = schema.validate(overlayJsonNode);
        if (validationMessages.isEmpty()) {
            return;
        }

        String errorMessage = validationMessages.stream()
                .sorted(Comparator.comparing(ValidationMessage::getMessage))
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining("; "));

        throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, errorMessage);
    }

    private JsonNode readJson(String overlayJson) {
        try {
            return objectMapper.readTree(overlayJson);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "overlay.json이 올바른 JSON 형식이 아닙니다.");
        }
    }

    private JsonSchema loadSchema(Resource schemaResource) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream inputStream = schemaResource.getInputStream()) {
            return factory.getSchema(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("overlay JSON schema를 불러오지 못했습니다.", exception);
        }
    }
}
