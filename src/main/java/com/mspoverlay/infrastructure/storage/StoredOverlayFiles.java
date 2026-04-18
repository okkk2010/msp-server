package com.mspoverlay.infrastructure.storage;

import java.nio.file.Path;

public record StoredOverlayFiles(
        String jsonPath,
        String thumbnailPath,
        Path overlayDirectory,
        Path jsonFilePath,
        Path thumbnailFilePath
) {
}
