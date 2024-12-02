package com.moa.store.domain.paymentInfo.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResultRequestDto {
	private UUID orderId;
	private List<PaymentResultDto> paymentInfo;
}
