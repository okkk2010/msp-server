package com.mspoverlay.infrastructure.storage;

import com.mspoverlay.global.config.StorageProperties;
import com.mspoverlay.global.exception.BusinessException;
import com.mspoverlay.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class OverlayStorageService {

    private static final String OVERLAYS_DIRECTORY = "overlays";
    private static final String JSON_FILE_NAME = "overlay.json";
    private static final String THUMBNAIL_FILE_NAME = "thumbnail.png";

    private final Path storageRoot;

    public OverlayStorageService(StorageProperties storageProperties) {
        this.storageRoot = Path.of(storageProperties.basePath()).toAbsolutePath().normalize();
    }

    public StoredOverlayFiles save(String overlayId, MultipartFile overlayJson, MultipartFile thumbnail) {
        validateOverlayDirectory(overlayId);
        validateOverlayJsonFile(overlayJson);
        validateThumbnailFile(thumbnail);

        Path overlayDirectory = storageRoot.resolve(OVERLAYS_DIRECTORY).resolve(overlayId).normalize();
        ensureWithinStorageRoot(overlayDirectory);

        try {
            Files.createDirectories(overlayDirectory);

            Path jsonFilePath = overlayDirectory.resolve(JSON_FILE_NAME);
            Path thumbnailFilePath = overlayDirectory.resolve(THUMBNAIL_FILE_NAME);

            copy(overlayJson, jsonFilePath);
            copy(thumbnail, thumbnailFilePath);

            return new StoredOverlayFiles(
                    "/storage/overlays/%s/%s".formatted(overlayId, JSON_FILE_NAME),
                    "/storage/overlays/%s/%s".formatted(overlayId, THUMBNAIL_FILE_NAME),
                    overlayDirectory,
                    jsonFilePath,
                    thumbnailFilePath
            );
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.FILE_SAVE_FAILED, "오버레이 파일 저장에 실패했습니다.");
        }
    }

    public void deleteDirectory(Path overlayDirectory) {
        if (overlayDirectory == null || Files.notExists(overlayDirectory)) {
            return;
        }

        try (var paths = Files.walk(overlayDirectory)) {
            paths.sorted((left, right) -> right.compareTo(left))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException exception) {
                            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "오버레이 파일 삭제에 실패했습니다.");
                        }
                    });
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, "오버레이 파일 삭제에 실패했습니다.");
        }
    }

    public StoredOverlayFiles replace(
            String overlayId,
            MultipartFile overlayJson,
            MultipartFile thumbnail,
            String currentJsonPath,
            String currentThumbnailPath
    ) {
        validateOverlayDirectory(overlayId);

        Path overlayDirectory = storageRoot.resolve(OVERLAYS_DIRECTORY).resolve(overlayId).normalize();
        ensureWithinStorageRoot(overlayDirectory);

        try {
            Files.createDirectories(overlayDirectory);

            Path jsonFilePath = overlayDirectory.resolve(JSON_FILE_NAME);
            Path thumbnailFilePath = overlayDirectory.resolve(THUMBNAIL_FILE_NAME);

            if (overlayJson != null && !overlayJson.isEmpty()) {
                validateOverlayJsonFile(overlayJson);
                copy(overlayJson, jsonFilePath);
            }
            if (thumbnail != null && !thumbnail.isEmpty()) {
                validateThumbnailFile(thumbnail);
                copy(thumbnail, thumbnailFilePath);
            }

            return new StoredOverlayFiles(
                    currentJsonPath != null ? currentJsonPath : "/storage/overlays/%s/%s".formatted(overlayId, JSON_FILE_NAME),
                    currentThumbnailPath != null ? currentThumbnailPath : "/storage/overlays/%s/%s".formatted(overlayId, THUMBNAIL_FILE_NAME),
                    overlayDirectory,
                    jsonFilePath,
                    thumbnailFilePath
            );
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.FILE_SAVE_FAILED, "오버레이 파일 저장에 실패했습니다.");
        }
    }

    public void deleteOverlayDirectory(String overlayId) {
        validateOverlayDirectory(overlayId);
        Path overlayDirectory = storageRoot.resolve(OVERLAYS_DIRECTORY).resolve(overlayId).normalize();
        ensureWithinStorageRoot(overlayDirectory);
        deleteDirectory(overlayDirectory);
    }

    private void copy(MultipartFile source, Path target) throws IOException {
        try (InputStream inputStream = source.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void validateOverlayDirectory(String overlayId) {
        if (overlayId == null || overlayId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "overlayId가 비어 있습니다.");
        }
        if (overlayId.contains("..") || overlayId.contains("/") || overlayId.contains("\\")) {
            throw new BusinessException(ErrorCode.INVALID_OVERLAY_JSON, "overlayId가 올바르지 않습니다.");
        }
    }

    private void ensureWithinStorageRoot(Path targetPath) {
        if (!targetPath.startsWith(storageRoot)) {
            throw new BusinessException(ErrorCode.FILE_SAVE_FAILED, "저장 경로가 올바르지 않습니다.");
        }
    }

    private void validateOverlayJsonFile(MultipartFile overlayJson) {
        if (overlayJson == null || overlayJson.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_MULTIPART_REQUEST, "overlayJson 파일이 필요합니다.");
        }
        String originalFilename = overlayJson.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".json")) {
            throw new BusinessException(ErrorCode.INVALID_MULTIPART_REQUEST, "overlayJson은 .json 파일만 업로드할 수 있습니다.");
        }
    }

    private void validateThumbnailFile(MultipartFile thumbnail) {
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_THUMBNAIL_FILE, "thumbnail 파일이 필요합니다.");
        }
        String originalFilename = thumbnail.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".png")) {
            throw new BusinessException(ErrorCode.INVALID_THUMBNAIL_FILE, "thumbnail은 .png 파일만 업로드할 수 있습니다.");
        }
    }
}
