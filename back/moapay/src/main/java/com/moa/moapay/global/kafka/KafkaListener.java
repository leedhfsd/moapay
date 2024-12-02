package com.moa.moapay.global.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.moapay.domain.dutchpay.model.vo.DutchPayCompleteVo;
import com.moa.moapay.domain.dutchpay.service.DutchPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaListener {

    private final DutchPayService dutchPayService;
    private final ObjectMapper objectMapper;

    @org.springframework.kafka.annotation.KafkaListener(topics = "tracking.dutchpay", groupId = "dutchpay_consumer_group")
    public void consume(String message) {
        try {
            Map<String, Object> map = objectMapper.readValue(message, Map.class);
            // JSON 메시지를 DutchPayCompliteVo 객체로 변환
            String dutchpayListJson = objectMapper.writeValueAsString(map.get("dutchpayList"));

            // JSON 메시지를 DutchPayCompliteVo 객체로 변환
            DutchPayCompleteVo dutchPayCompleteVo = objectMapper.readValue(dutchpayListJson, DutchPayCompleteVo.class);

            // 변환된 객체를 서비스로 넘겨 처리
            dutchPayService.dutchpayComplite(dutchPayCompleteVo);
            log.info("Received message: {}", dutchPayCompleteVo);
        } catch (Exception e) {
            log.error("Error while processing message: {}", message, e);
        }
    }
}
