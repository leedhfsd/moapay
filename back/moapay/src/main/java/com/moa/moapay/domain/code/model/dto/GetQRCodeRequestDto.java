package com.moa.moapay.domain.code.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class
GetQRCodeRequestDto {
    private UUID orderId;
    private UUID merchantId;
    private String merchantName;
    private String categoryId;
    private long totalPrice;
}
