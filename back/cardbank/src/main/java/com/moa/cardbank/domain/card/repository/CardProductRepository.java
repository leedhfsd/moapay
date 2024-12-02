package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardProductRepository extends JpaRepository<CardProduct, Long> {
    <S extends CardProduct> S save(S cardProduct);
    Optional<CardProduct> findByUuid(UUID uuid);
}
