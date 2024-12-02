package com.moa.store.domain.order.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemDto {
    private UUID itemId;
    private Long quantity;
    private long price;
}
