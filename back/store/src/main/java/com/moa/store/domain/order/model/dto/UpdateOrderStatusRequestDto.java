package com.moa.store.domain.order.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateOrderStatusRequestDto {
    private UUID orderId;
    private String status;
}
