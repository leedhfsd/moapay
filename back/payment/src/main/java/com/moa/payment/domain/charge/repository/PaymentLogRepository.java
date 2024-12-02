package com.moa.payment.domain.charge.repository;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.moa.payment.domain.charge.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

    <S extends PaymentLog> S save(S paymentLog);
    Optional<PaymentLog> findByUuid(UUID uuid);

    @Query("SELECT p FROM PaymentLog p " +
            "WHERE YEAR(p.createTime) = YEAR(CURRENT_DATE) " +
            "AND MONTH(p.createTime) = MONTH(CURRENT_DATE) - 1")
    List<PaymentLog> findAllFromLastMonth();

    @Query("SELECT p FROM PaymentLog p " +
            "WHERE p.cardId = :cardId " +
            "AND FUNCTION('YEAR', p.createTime) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', p.createTime) = FUNCTION('MONTH', CURRENT_DATE)")
    List<PaymentLog> findAllFromMonth(@Param("cardId") UUID cardId);
    //
    // @Query("SELECT p FROM PaymentLog p " +
    //     "WHERE p.cardId = :cardId " +
    //     "AND FUNCTION('YEAR', p.createTime) = FUNCTION('YEAR', FUNCTION('DATE_SUB', CURRENT_DATE, 1, 'MONTH')) " +
    //     "AND FUNCTION('MONTH', p.createTime) = FUNCTION('MONTH', FUNCTION('DATE_SUB', CURRENT_DATE, 1, 'MONTH'))")
    // List<PaymentLog> findAllFromMonth(@Param("cardId") UUID cardId);

    @Query("SELECT p.categoryId, COUNT(p) FROM PaymentLog p WHERE p.cardId = :cardId AND p.status != 'CANCELED' GROUP BY p.categoryId")
    List<Object[]> countByCategoryIdGroupedByCardId(@Param("cardId") UUID cardId);

}
