package com.moa.moapay.domain.card.repository;

import com.moa.moapay.domain.card.entity.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardProductRepository extends JpaRepository<CardProduct, Long> {

    // 카드와 혜택을 JOIN하여 한 번에 가져오기
    @Query("SELECT cp FROM CardProduct cp " +
            "JOIN FETCH cp.benefits cb")
    List<CardProduct> findAllWithBenefits();

    CardProduct findByUuid(UUID uuid);
}
