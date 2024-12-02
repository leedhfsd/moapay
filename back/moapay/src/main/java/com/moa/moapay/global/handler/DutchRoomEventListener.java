package com.moa.moapay.global.handler;

import com.moa.moapay.domain.dutchpay.service.DutchPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DutchRoomEventListener {

    private final DutchPayService dutchPayService;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener  // 트랜잭션이 커밋된 후에 이벤트 리스닝
    public void handleDutchRoomInfoEvent(DutchRoomInfoEvent event) {
        UUID roomId = event.getRoomId();
        dutchPayService.getDutchRoomInfo(roomId); // 트랜잭션 완료 후 방 정보 전송
    }
}
