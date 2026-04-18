package com.mspoverlay.global.response;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "ok");
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, "ok");
    }
}
