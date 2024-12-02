package com.moa.store.domain.paymentInfo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.store.domain.notification.model.ResultStatus;
import com.moa.store.domain.notification.model.dto.NotifyDto;
import com.moa.store.domain.notification.service.NotificationService;
import com.moa.store.domain.order.model.Order;
import com.moa.store.domain.order.model.OrderStatus;
import com.moa.store.domain.order.model.dto.CreateOrderRequestDto;
import com.moa.store.domain.order.model.dto.CreateOrderResponseDto;
import com.moa.store.domain.order.repository.OrderRepository;
import com.moa.store.domain.order.service.OrderService;
import com.moa.store.domain.paymentInfo.model.PaymentInfo;
import com.moa.store.domain.paymentInfo.model.ProcessingStatus;
import com.moa.store.domain.paymentInfo.model.dto.GetQRCodeResponseDto;
import com.moa.store.domain.paymentInfo.model.dto.PaymentResultDto;
import com.moa.store.domain.paymentInfo.model.dto.PaymentResultRequestDto;
import com.moa.store.domain.paymentInfo.repository.PaymentInfoRepository;
import com.moa.store.global.exception.BusinessException;
import feign.FeignException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moa.store.domain.paymentInfo.client.getQRCodeFromMoaPayDto;
import com.moa.store.domain.paymentInfo.client.MoaPayClient;
import com.moa.store.global.response.ResultResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentInfoServiceImpl implements PaymentInfoService {

	private final MoaPayClient moaPayClient;
	private final ObjectMapper objectMapper;
	private final PaymentInfoRepository paymentInfoRepository;
	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public GetQRCodeResponseDto getQRCode(CreateOrderRequestDto createOrderRequestDto) {
//		try {
		log.info("create order and get qrcode...");
		log.info("order request dto : {}", createOrderRequestDto.toString());
			CreateOrderResponseDto createOrderResponseDto = orderService.createOrder(createOrderRequestDto);
			ResponseEntity<ResultResponse> getQRcode = moaPayClient.getQRCode(createOrderResponseDto);
			if(getQRcode.getStatusCode() != HttpStatus.OK){
				throw new BusinessException(HttpStatus.BAD_REQUEST, "QR 발급 중 문제가 발생했습니다.");
			}
			getQRCodeFromMoaPayDto getQRCodeFromMoaPayDto = objectMapper.convertValue((getQRcode.getBody().getData()), getQRCodeFromMoaPayDto.class);
			log.info("getQRCodeFromMoaPayDto:{}", getQRCodeFromMoaPayDto);
            return GetQRCodeResponseDto.builder()
					.orderId(createOrderResponseDto.getOrderId())
					.QRCode(getQRCodeFromMoaPayDto.getQRCode())
					.build();

			// client에서는 QR 생성과 동시에 Id에 맞는 SSE 구독을 시행 (orderId를 기준으로 해도 될듯)
//		} catch (FeignException e) {
//			throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 생성 중 오류가 발생했습니다(Feign 문제)");
//		} catch (Exception e) {
//			throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
//		}
	}

	@Override
	@Transactional
	public void savePaymentInfo(PaymentResultRequestDto paymentResultRequestDto) {
		Order order = orderRepository.findByOrderId(paymentResultRequestDto.getOrderId())
			.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "주문 UUID를 확인해주세요"));
		// 결제정보를 확인하면서, 결제가 다 이루어졌는지를 판단한다
		if(order.getState() == OrderStatus.OrderCancel || order.getState() == OrderStatus.OrderComplete || order.getState() == OrderStatus.PayComplete) {
			// 결제 진행중이 아닌 order에 대해 결제가 시도되었다면 돌려보낸다
			throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
		}
		// 결제 진행중이라면, 기존 결제정보와 현재 값의 종합을 통해 결제가 완료되었는지를 판단해야 한다
		long totalAmount = 0; // actual_amount가 아닌 그냥 amount를 통해 판단
		for(PaymentInfo info : order.getPaymentInfos()) {
			totalAmount += info.getAmount();
		}
		for(PaymentResultDto paymentResult : paymentResultRequestDto.getPaymentInfo()) {
			PaymentInfo paymentInfo = PaymentInfo.builder()
					.order(order)
					.cardNumber(paymentResult.getCardNumber())
					.amount(paymentResult.getAmount())
					.actualAmount(paymentResult.getActualAmount())
					.status(ProcessingStatus.APPROVED)
					.build();
			paymentInfoRepository.save(paymentInfo);
			totalAmount += paymentResult.getAmount();
		}
		// 저장 종료
		// 만일 결제가 완료되었다면 그에 따른 프로세스 진행
		if(totalAmount >= order.getTotalPrice()) {
			log.info("all payments are completed");
			order.updateState("PayComplete");
			NotifyDto notifyDto = NotifyDto.builder()
					.orderId(order.getUuid())
					.status(ResultStatus.SUCCEED)
					.build();
			notificationService.sendMessage(order.getUuid(), notifyDto);
		} else {
			// 다 끝난 게 아니라면 결제 프로세스를 진행중으로 변경
			log.info("payment is in process... ");
			log.info("remaining amount : {}", order.getTotalPrice() - totalAmount);
			order.updateState("PayWaiting");
		}
	}
}
