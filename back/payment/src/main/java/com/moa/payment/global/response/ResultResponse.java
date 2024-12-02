package com.moa.payment.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    private HttpStatus status;
    private String message;
    private Object data;

    public static ResultResponse of(HttpStatus status, String message, Object data) {
        return ResultResponse.builder()
            .status(status)
            .message(message)
            .data(data)
            .build();
    }

    public static ResultResponse of(HttpStatus status, String message) {
        return ResultResponse.builder()
            .status(status)
            .message(message)
            .build();
    }
}

