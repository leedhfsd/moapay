package com.moa.store.domain.paymentInfo.service;

import com.moa.store.domain.order.model.dto.CreateOrderRequestDto;
import com.moa.store.domain.paymentInfo.client.getQRCodeFromMoaPayDto;
import com.moa.store.domain.paymentInfo.model.dto.GetQRCodeResponseDto;
import com.moa.store.domain.paymentInfo.model.dto.PaymentResultRequestDto;

public interface PaymentInfoService {
	GetQRCodeResponseDto getQRCode(CreateOrderRequestDto createOrderRequestDto);
	void savePaymentInfo(PaymentResultRequestDto paymentResultRequestDto);
	//OrderResponseDto offlinePayment(ExecuteOfflinePayRequestDto executeOfflinePayRequestDto);
}
