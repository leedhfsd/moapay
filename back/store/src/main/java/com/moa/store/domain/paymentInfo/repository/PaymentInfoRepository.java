package com.moa.store.domain.paymentInfo.repository;

import com.moa.store.domain.order.model.Order;
import com.moa.store.domain.paymentInfo.model.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    List<PaymentInfo> findByOrder(Order order);
}
