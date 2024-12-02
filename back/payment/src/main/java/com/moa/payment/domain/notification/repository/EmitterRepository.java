package com.moa.payment.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    // 동시성 문제 처리를 위해 thread-safe한 자료구조 사용
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    // HashMap 관리를 위한 메서드들
    public void save(UUID id, SseEmitter emitter) {
        emitters.put(id, emitter);
    }

    public void deleteById(UUID id) {
        emitters.remove(id);
    }

    public SseEmitter getById(UUID id) {
        return emitters.get(id);
    }

    public int size() {
        return emitters.size();
    }

}
