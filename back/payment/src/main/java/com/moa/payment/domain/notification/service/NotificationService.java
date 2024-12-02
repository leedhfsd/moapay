package com.moa.payment.domain.notification.service;

import com.moa.payment.domain.charge.model.dto.PaymentResultDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.UUID;

public interface NotificationService {

//    <T> void sendToClient(UUID code, T data, String comment);
    void initialSend(UUID code, SseEmitter emitter);
    SseEmitter createEmitter(UUID id);
    SseEmitter subscribe(UUID id);
    void sendCompleteMessage(UUID id, PaymentResultDto resultDto);
//    void sendEvent(UUID sendId, Object data);
}
