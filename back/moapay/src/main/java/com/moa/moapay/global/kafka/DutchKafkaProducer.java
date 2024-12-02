package com.moa.moapay.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DutchKafkaProducer {

    private static final String TOPIC = "request.dutchCancel";
    private final KafkaTemplate<String, Map> kafkaTemplate;

    public void send(Map<String, Object> message, String key) {
        log.info("send message via Map");
        this.kafkaTemplate.send(TOPIC, "1", message);
    }
}
