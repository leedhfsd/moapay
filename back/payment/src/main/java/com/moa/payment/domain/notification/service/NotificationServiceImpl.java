package com.moa.payment.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.charge.model.dto.PaymentResultDto;
import com.moa.payment.domain.notification.listener.RedisSubscribeListener;
import com.moa.payment.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    //타임아웃 설정
    private static final long DEFAULT_TIMEOUT = 20L * 1000 * 60; // timeout은 약 20분
    private final EmitterRepository emitterRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscribeListener redisSubscribeListener;
    private final RedisTemplate<String, Object> redisEmitterTemplate;

    @Override
    public SseEmitter subscribe(UUID code) {
        SseEmitter emitter = createEmitter(code);
        initialSend(code, emitter);
        log.info("sendToClient done");
        // emitter 저장 이후, 주어진 코드를 기반으로 listener가 구독하도록 지시
        redisMessageListenerContainer.addMessageListener(redisSubscribeListener, new ChannelTopic(code.toString()));
        return emitter;
    }

    @Override
    public void sendCompleteMessage(UUID id, PaymentResultDto resultDto) {
        log.info("send charge complete message: {} - {}", id.toString(), resultDto);
        // redis publish를 이용해 emitter를 갖고있을 listener에게 메시지를 보낸다
        Long subscriberCount = redisEmitterTemplate.convertAndSend(id.toString(), resultDto);
        log.info("convert and send done");
        log.info("subscriberCount: {}", subscriberCount);
    }

    @Override
    public void initialSend(UUID code, SseEmitter emitter) {
        if (emitter != null) {
            try {
                String jsonData = new ObjectMapper().writeValueAsString("EventStream Created. [code="+code.toString()+"]"); // 데이터를 JSON으로 변환
                emitter.send(SseEmitter.event()
                        .id(code.toString())
                        .name("sse")
                        .data(jsonData) // 변환된 JSON 데이터를 전송
                        .comment("sse 접속 성공"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }

    @Override
    public SseEmitter createEmitter(UUID id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 어차피 timeout이 지나면 사라지므로 따로 삭제 처리는 하지 않는다
        emitterRepository.save(id, emitter);
        emitter.onCompletion(() -> {emitterRepository.deleteById(id); log.info("emitter completed - current size : {}", emitterRepository.size());});
        emitter.onTimeout(() -> {emitterRepository.deleteById(id); log.info("emitter timeout - current size : {}", emitterRepository.size());});
        log.info("added new emitter - current emitterRepository size : {}", emitterRepository.size());
        return emitter;
    }
}
