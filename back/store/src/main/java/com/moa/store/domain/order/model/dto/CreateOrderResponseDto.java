package com.moa.store.domain.order.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponseDto {
    private UUID orderId;
    private UUID merchantId;
    private String merchantName;
    private String categoryId;
    private long totalPrice;
}
