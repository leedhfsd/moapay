package com.moa.store.domain.order.service;

import com.moa.store.domain.order.model.dto.*;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderListResponseDto> getOrdersByMerchant(UUID merchantId);
    OrderResponseDto getOrderResponse(UUID orderId);
    OrderResponseDto ChangeOrderStatus(UpdateOrderStatusRequestDto updateOrderStatusRequestDto);
    CreateOrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto);
    GetSimpleOrderResponseDto getSimpleOrder(UUID orderId);
}
