package com.minh.common.exception;

import com.minh.common.constants.ErrorCode;
import com.minh.common.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<ResponseData> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn(ex.getMessage());
        ex.printStackTrace();
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                Map.of(
                        "errorCode", ErrorCode.INVALID_REQUEST,
                        "resourcePath", ex.getResourcePath()
                )
        );
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<ResponseData> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Internal server error",
                Map.of("errorCode", ErrorCode.INTERNAL_SERVER_ERROR)
        );
    }

    private ResponseEntity<ResponseData> buildResponse(HttpStatusCode status, String message, Object data) {
        return ResponseEntity
                .status(status)
                .body(ResponseData.builder()
                        .status(status.value())
                        .message(message)
                        .data(data)
                        .build());
    }
}
