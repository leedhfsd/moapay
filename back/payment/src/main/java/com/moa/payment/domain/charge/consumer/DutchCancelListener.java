package com.moa.payment.domain.charge.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.charge.model.vo.DutchCancelRequestVo;
import com.moa.payment.domain.charge.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DutchCancelListener {

    private final ChargeService chargeService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "request.dutchCancel", groupId = "dutchCancel_consumer_group")
    public void consume(String record) {
        try {
            log.info("{}", record);
            Map<String, Object> map = objectMapper.readValue(record, Map.class);
            // 메시지를 UUID로 변환
            UUID uuid = UUID.fromString((String) map.get("paymentId"));

            // 변환된 UUID 값 처리 로직
            log.info("Consumed UUID: {}", uuid);

            chargeService.dutchCancel(uuid);

        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: ", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
