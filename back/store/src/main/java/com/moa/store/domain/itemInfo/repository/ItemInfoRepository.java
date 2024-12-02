package com.moa.store.domain.itemInfo.repository;

import com.moa.store.domain.itemInfo.model.ItemInfo;
import com.moa.store.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemInfoRepository extends JpaRepository<ItemInfo, Long> {
    List<ItemInfo> findByOrder(Order order);
}
