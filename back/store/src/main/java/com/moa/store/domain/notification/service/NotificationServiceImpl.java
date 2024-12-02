package com.moa.store.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.store.domain.notification.model.dto.NotifyDto;
import com.moa.store.domain.notification.repository.EmitterRepository;
import com.moa.store.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final long DEFAULT_TIMEOUT = 20L * 1000 * 60; // timeout은 약 20분
    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    @Override
    public SseEmitter subscribe(UUID id) {
        SseEmitter emitter = createEmitter(id);
        initialSend(id, emitter);
        log.info("sendToClient done");
        return emitter;
    }

    @Override
    public void initialSend(UUID code, SseEmitter emitter) {
        if(emitter == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "emitter 생성에 실패했습니다.");
        }
        try {
            String jsonData = objectMapper.writeValueAsString("EventStream Created. [code="+code.toString()+"]"); // 데이터를 JSON으로 변환
            emitter.send(SseEmitter.event()
                    .id(code.toString())
                    .name("sse")
                    .data(jsonData) // 변환된 JSON 데이터를 전송
                    .comment("sse 접속 성공"));
        } catch(IOException e) {
            emitter.completeWithError(e);
        }
    }

    @Override
    public SseEmitter createEmitter(UUID id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 어차피 timeout이 지나면 사라지므로 따로 삭제 처리는 하지 않는다
        emitterRepository.save(id, emitter);
        emitter.onCompletion(() -> log.info("emitter completed"));
        emitter.onTimeout(() -> log.info("emitter timeout"));
        log.info("added new emitter");
        return emitter;
    }

    @Override
    public void sendMessage(UUID id, NotifyDto dto) {
        log.info("send payment complete message to store : {} - {}", id.toString(), dto);
        SseEmitter emitter = emitterRepository.getById(id);
        if(emitter == null) {
            log.error("SSE emitter not found"); // 메시지를 보낼 수 없는 경우, 보내지 않고 종료하는 것으로...
            return;
//            throw new BusinessException(HttpStatus.BAD_REQUEST, "SSE emitter not found");
        }
        try {
            emitter.send(SseEmitter.event().id(id.toString()).name("payment-result").data(dto));
            // 결제 프로세스가 완료되면 더이상 구독은 필요 없으므로 emitter를 종료시킨다
            emitter.complete();
        } catch(IOException e) {
            e.printStackTrace();
            emitter.completeWithError(e);
        }
    }
}
