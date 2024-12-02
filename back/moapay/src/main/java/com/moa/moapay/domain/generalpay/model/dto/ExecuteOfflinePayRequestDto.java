package com.moa.moapay.domain.generalpay.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ExecuteOfflinePayRequestDto {
    private UUID orderId;
    private UUID requestId;
    private UUID merchantId;
    private long totalPrice;
    private String barcode;
}
