package com.moa.store.domain.order.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderListResponseDto {
    private UUID orderId;
    private String customerId;
    private long totalPrice;
    private String state;
    private TitleItemDto titleItem;
    private int itemCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
