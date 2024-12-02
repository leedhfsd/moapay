package com.moa.store.domain.paymentInfo.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class getQRCodeFromMoaPayDto {
	private String QRCode;
}