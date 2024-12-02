package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.EarningLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EarningLogRepository extends JpaRepository<EarningLog, Long> {
    <S extends EarningLog> S save(S earningLog);
    List<EarningLog> findByPaymentLogId(Long paymentLogId);
}
