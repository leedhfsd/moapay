package com.moa.store.domain.paymentInfo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetQRCodeResponseDto {
    private UUID orderId;
    private String QRCode;
}
