package com.moa.payment.domain.notification.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.charge.model.dto.PaymentResultDto;
import com.moa.payment.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
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
public class RedisSubscribeListener implements MessageListener {

    private final RedisTemplate<String, Object> redisEmitterTemplate;
    private final ObjectMapper objectMapper;
    private final EmitterRepository emitterRepository;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = redisEmitterTemplate.getStringSerializer().deserialize(message.getBody());
        log.info("received message : {}", publishMessage);
        // 이중 try문... 이대로 괜찮은가...?
        try {
            PaymentResultDto resultDto = objectMapper.readValue(publishMessage, PaymentResultDto.class);
            log.info("received resultDto {}", resultDto.toString());
            UUID code = resultDto.getRequestId();
            SseEmitter emitter = emitterRepository.getById(code);
            if (emitter == null) {
                log.info("cannot found emitter : {}", code.toString());
            }
            try {
                emitter.send(SseEmitter.event().id(code.toString()).name("payment-completed").data(resultDto));
                // 결제 프로세스가 완료되면 더이상 구독은 필요 없으므로 emitter를 종료시킨다
                emitter.complete();
                // 이후 해당 토픽에 대한 구독 상태 해제
                log.info("remove message listener...");
                redisMessageListenerContainer.removeMessageListener(this, new ChannelTopic(code.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                emitter.completeWithError(e);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
