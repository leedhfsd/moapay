package com.moa.payment.domain.charge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendResultToStoreRequestDto {
    private UUID orderId;
    private List<StoreResultDto> paymentInfo;
}
