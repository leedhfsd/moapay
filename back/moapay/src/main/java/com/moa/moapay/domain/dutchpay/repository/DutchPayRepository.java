package com.moa.moapay.domain.dutchpay.repository;

import com.moa.moapay.domain.dutchpay.entity.DutchPay;
import com.moa.moapay.domain.dutchpay.entity.DutchRoom;
import com.moa.moapay.domain.dutchpay.entity.DutchStatus;
import jakarta.ws.rs.DELETE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface DutchPayRepository extends JpaRepository<DutchPay, Long> {
    public DutchPay findByUuid(UUID uuid);

    @Modifying
    @Transactional
    @Query("UPDATE DutchPay p SET p.amount = :amount, p.payStatus = :status WHERE p.memberId = :memberId AND p.roomEntity.uuid = :roomUuid")
    void updateAmountByMemberId(@Param("amount") Long amount, @Param("memberId") UUID memberId, @Param("roomUuid") UUID roomUuid, @Param("status") DutchStatus status);

    @Query("SELECT p FROM DutchPay p JOIN FETCH p.roomEntity r WHERE r.uuid = :roomUuid")
    List<DutchPay> findByRoomUuid(UUID roomUuid);

    // 삭제 어떤걸로?
    @Modifying
    @Transactional
    @Query("DELETE FROM DutchPay p WHERE p.memberId = :memberId AND p.roomEntity.uuid = :roomUuid")
    void deleteDutchPayByUuid(@Param("memberId") UUID memberId, @Param("roomUuid") UUID roomUuid);

    // 상태변경
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE DutchPay p Set p.payStatus = :status WHERE p.memberId = :memberId AND p.roomEntity.uuid = :roomUuid")
    void updateStatus(@Param("memberId") UUID memberId, @Param("roomUuid") UUID roomUuid, @Param("status") DutchStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE DutchPay p SET p.payStatus = :payStatus WHERE p.uuid = :dutchUuid" )
    void updateByDutchUuid(@Param("dutchUuid") UUID dutchUuid, DutchStatus payStatus);

    @Query("SELECT p FROM DutchPay p JOIN FETCH p.roomEntity r WHERE r.uuid = :roomId and p.memberId = :memberId")
    DutchPay findByRoomIdAndMemberId(@Param("roomId") UUID roomId, @Param("memberId") UUID memberId);
}
