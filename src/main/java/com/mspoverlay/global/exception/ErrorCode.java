package com.mspoverlay.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력 오류"),
    INVALID_MULTIPART_REQUEST(HttpStatus.BAD_REQUEST, "multipart 요청 오류"),
    INVALID_OVERLAY_JSON(HttpStatus.BAD_REQUEST, "overlay.json 검증 실패"),
    INVALID_THUMBNAIL_FILE(HttpStatus.BAD_REQUEST, "썸네일 오류"),
    INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "플랫폼 오류"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh Token을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "플랫폼을 찾을 수 없습니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다."),
    OVERLAY_NOT_FOUND(HttpStatus.NOT_FOUND, "오버레이를 찾을 수 없습니다."),
    LIBRARY_NOT_FOUND(HttpStatus.NOT_FOUND, "라이브러리를 찾을 수 없습니다."),
    OVERLAY_CODE_DUPLICATED(HttpStatus.CONFLICT, "오버레이 코드가 이미 존재합니다."),
    OVERLAY_ALREADY_SAVED(HttpStatus.CONFLICT, "이미 저장된 오버레이입니다."),
    LIBRARY_ALREADY_SAVED(HttpStatus.CONFLICT, "이미 저장한 오버레이입니다."),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
