package com.moa.gateway.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;

    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
    }
}
