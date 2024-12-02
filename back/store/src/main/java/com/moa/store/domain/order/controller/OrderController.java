package com.moa.store.domain.order.controller;

import com.moa.store.domain.order.model.Order;
import com.moa.store.domain.order.model.dto.*;
import com.moa.store.domain.order.service.OrderService;
import com.moa.store.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:5173") // 프론트엔드 도메인
@RestController
@RequestMapping("/store/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성, API 명세서 7행
    @PostMapping("/create")
    public ResponseEntity<ResultResponse> createOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        CreateOrderResponseDto createOrderResponseDto = orderService.createOrder(createOrderRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.CREATED, "주문이 생성됐습니다.", createOrderResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    // 가맹점 별 주문 조회, API 명세서 8행
    @GetMapping("/list/{merchantId}")
    public ResponseEntity<ResultResponse> getOrdersByMerchant(@PathVariable("merchantId") UUID merchantId) {
        List<OrderListResponseDto> orderListResponseDto = orderService.getOrdersByMerchant(merchantId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "주문 상세 조회를 완료했습니다.", orderListResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/simple/{orderId}")
    public ResponseEntity<ResultResponse> getOrderSimple(@PathVariable("orderId") UUID orderId) {
        GetSimpleOrderResponseDto responseDto = orderService.getSimpleOrder(orderId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "주문의 간략한 정보를 조회했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    // 주문 상세 조회, API 명세서 9행
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<ResultResponse> getOrderDetail(@PathVariable("orderId") UUID orderId) {
        OrderResponseDto orderResponseDto = orderService.getOrderResponse(orderId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "주문 상세 조회를 완료했습니다.", orderResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    // 주문 상태 수정, API 명세서 10행
    @PutMapping("/status")
    public ResponseEntity<ResultResponse> updateOrderStatus(@RequestBody UpdateOrderStatusRequestDto updateOrderStatusRequestDto) {
        OrderResponseDto orderResponseDto = orderService.ChangeOrderStatus(updateOrderStatusRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "주문 상태 수정을 완료했습니다.", orderResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
