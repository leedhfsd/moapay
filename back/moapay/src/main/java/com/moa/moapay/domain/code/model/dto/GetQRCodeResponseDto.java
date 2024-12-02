package com.moa.moapay.domain.code.model.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetQRCodeResponseDto {
    private String QRCode;
}
