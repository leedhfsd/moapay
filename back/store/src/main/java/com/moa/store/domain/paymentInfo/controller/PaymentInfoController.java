package com.moa.store.domain.paymentInfo.controller;

import com.moa.store.domain.order.model.dto.CreateOrderRequestDto;
import com.moa.store.domain.paymentInfo.model.dto.GetQRCodeResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moa.store.domain.paymentInfo.client.getQRCodeFromMoaPayDto;
import com.moa.store.domain.paymentInfo.model.dto.PaymentResultRequestDto;
import com.moa.store.domain.paymentInfo.service.PaymentInfoService;
import com.moa.store.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/payment")
@RequiredArgsConstructor
public class PaymentInfoController {

	private final PaymentInfoService paymentInfoService;

	// 오프라인 결제, API 명세서 2행
	// @PostMapping("/offline/barcode")
	// public ResponseEntity<ResultResponse> offlineBarcodePayRequest(@RequestBody String barcode) {
	// 	ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "오프라인 결제");
	// 	return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	// }
	
	// todo : 오프라인쪽 결제 매커니즘 연결

	// 온라인 구매, API 명세서 3행
	@PostMapping("/online/purchase")
	public ResponseEntity<ResultResponse> onlinePurchasePayRequest(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
		GetQRCodeResponseDto QRCodeResponseDto = paymentInfoService.getQRCode(createOrderRequestDto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "QR코드 발급이 완료되었습니다.", QRCodeResponseDto);
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}

	// 결제 결과 저장, API 명세서 4행
	@PostMapping("/online/result")
	public ResponseEntity<ResultResponse> savePayment(@RequestBody PaymentResultRequestDto paymentResultRequestDto) {
		paymentInfoService.savePaymentInfo(paymentResultRequestDto);
		ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "결제 내용을 저장했습니다.");
		return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
	}
}