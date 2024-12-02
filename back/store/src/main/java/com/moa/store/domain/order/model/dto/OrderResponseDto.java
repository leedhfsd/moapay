package com.moa.store.domain.order.model.dto;

import com.moa.store.domain.itemInfo.model.dto.ItemInfoDto;
import com.moa.store.domain.paymentInfo.model.dto.PaymentInfoDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDto {
    private UUID orderId;
    private String customerId;
    private long totalPrice;
    private long totalAmount;
    private long totalActualAmount;
    private String state;
    private int itemCount;
    private List<ItemInfoDto> itemInfoList;
    private List<PaymentInfoDto> paymentInfoList;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
