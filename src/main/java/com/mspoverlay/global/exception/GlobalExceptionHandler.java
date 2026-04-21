package com.mspoverlay.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.mspoverlay.global.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        if (errorCode.status().is5xxServerError()) {
            log.error("Business exception on {} {} [{}]: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    errorCode.name(),
                    exception.getMessage(),
                    exception);
        } else {
            log.warn("Business exception on {} {} [{}]: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    errorCode.name(),
                    exception.getMessage());
        }
        return ResponseEntity
                .status(errorCode.status())
                .body(ErrorResponse.of(errorCode.name(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT.name(), message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = "%s 값이 올바르지 않습니다.".formatted(exception.getName());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT.name(), message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT.name(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.status())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR.name(), ErrorCode.INTERNAL_SERVER_ERROR.defaultMessage()));
    }

    private String formatFieldError(FieldError fieldError) {
        if (fieldError.getDefaultMessage() == null || fieldError.getDefaultMessage().isBlank()) {
            return "%s 값이 올바르지 않습니다.".formatted(fieldError.getField());
        }
        return "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
