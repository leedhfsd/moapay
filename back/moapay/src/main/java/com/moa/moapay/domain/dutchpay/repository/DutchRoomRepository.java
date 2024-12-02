package com.moa.moapay.domain.dutchpay.repository;

import com.moa.moapay.domain.dutchpay.entity.DutchRoom;
import com.moa.moapay.domain.dutchpay.entity.DutchStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DutchRoomRepository extends JpaRepository<DutchRoom, Long> {

    DutchRoom findByUuid(UUID roomId);

    @Modifying
    @Transactional
    @Query("UPDATE DutchRoom r SET r.curPerson = :memberCnt, r.status = :status WHERE r.uuid = :roomId")
    void updateDutchRoomConfirm(long memberCnt, DutchStatus status, UUID roomId);

    @Modifying
    @Transactional
    @Query("UPDATE DutchRoom  r SET r.status = :status")
    void updateDutchRoomStatus(DutchStatus status);

    @Query("SELECT r FROM DutchRoom r LEFT JOIN FETCH r.dutchPayList p WHERE r.uuid = :uuid")
    DutchRoom findByRoomId(UUID uuid);


    @Modifying
    @Transactional
    @Query("UPDATE DutchRoom r SET r.curPerson = r.curPerson + 1 WHERE r.uuid = :roomId")
    void incrementParticipantCount(UUID roomId);

    @Modifying
    @Transactional
    @Query("UPDATE DutchRoom r SET r.curPerson = r.curPerson - 1 WHERE r.uuid = :roomId AND r.curPerson > 0")
    void decrementParticipantCount(UUID roomId);

    @Query("SELECT r FROM DutchRoom r JOIN FETCH r.dutchPayList dp WHERE dp.uuid = :uuid")
    DutchRoom findByDutchUuid(UUID uuid);

}
