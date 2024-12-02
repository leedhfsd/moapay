package com.moa.store.domain.paymentInfo.client;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecuteOfflinePayRequestDto {
	private UUID requestId;
	private UUID orderId;
	private UUID merchantId;
	private long totalPrice;
	private String barcode;
}
