package com.moa.store.domain.paymentInfo.client;

import com.moa.store.domain.order.model.dto.CreateOrderResponseDto;
import com.moa.store.global.response.ResultResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "moapay", url = "https://j11c201.p.ssafy.io/api")
public interface MoaPayClient {
	@PostMapping("moapay/core/code/QRcode")
	ResponseEntity<ResultResponse> getQRCode(@RequestBody CreateOrderResponseDto createOrderResponseDto);

	@PostMapping("moapay/core/generalpay/offline")
	ResponseEntity<ResultResponse> executeOfflinePay
		(@RequestBody ExecuteOfflinePayRequestDto executeOfflinePayRequestDto);

}
