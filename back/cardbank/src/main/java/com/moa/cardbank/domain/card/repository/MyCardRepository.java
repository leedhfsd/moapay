package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.MyCard;
import com.moa.cardbank.domain.card.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyCardRepository extends JpaRepository<MyCard, Long> {
    Optional<MyCard> findByUuid(UUID uuid);
    <S extends MyCard> S save(S myCard);
    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT mc FROM MyCard mc " +
            "JOIN FETCH mc.product cp " +
            "JOIN FETCH mc.account ac " +
            "JOIN FETCH mc.member mb " +
            "WHERE mb.phoneNumber = :phoneNumber")
    List<MyCard> findByPhoneNumber(String phoneNumber);

    // PaymentLog를 별도로 가져오는 쿼리
    @Query("SELECT pl FROM PaymentLog pl WHERE pl.card.id IN " +
            "(SELECT mc.id FROM MyCard mc JOIN mc.member mb WHERE mb.phoneNumber = :phoneNumber)")
    List<PaymentLog> findPaymentLogsByMemberId(String phoneNumber);

    @Query("SELECT mc FROM MyCard mc " +
           "JOIN FETCH mc.product cp " +
           "JOIN FETCH mc.account ac " +
           "JOIN FETCH mc.member mb " +
           "WHERE mc.cardNumber = :cardNumber")
    MyCard findByCardNumber(String cardNumber);
}
