package com.moa.store.domain.notification.service;

import com.moa.store.domain.notification.model.dto.NotifyDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface NotificationService {
    SseEmitter subscribe(UUID id);
    void initialSend(UUID code, SseEmitter emitter);
    SseEmitter createEmitter(UUID id);
    void sendMessage(UUID id, NotifyDto dto);
}
