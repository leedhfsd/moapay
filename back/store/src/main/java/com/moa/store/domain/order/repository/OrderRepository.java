package com.moa.store.domain.order.repository;

import com.moa.store.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreUuid(UUID merchantId);
    Optional<Order> findByUuid(UUID orderId);
    @Query("select o from Order o left join fetch o.itemInfos where o.store.uuid = :merchantId")
    List<Order> findByStoreUuidWithItemInfos(UUID merchantId);
    @Query("select o from Order o left join fetch o.paymentInfos where o.uuid = :orderId")
    Optional<Order> findByOrderId(UUID orderId);
    @Query("select distinct o from Order o left join fetch o.itemInfos where o.uuid = :orderId")
    Optional<Order> findByOrderIdWithItemInfos(UUID orderId);
    
}
