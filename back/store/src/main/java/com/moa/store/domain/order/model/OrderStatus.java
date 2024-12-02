package com.moa.store.domain.order.model;

// 주문접수, 결제대기, 결제완료, 주문완료, 주문취소
public enum OrderStatus {
	OrderCreated, PayWaiting, PayComplete, OrderComplete, OrderCancel
}
