package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    <S extends PaymentLog> S save(S paymentLog);
    Optional<PaymentLog> findByUuid(UUID uuid);
}
