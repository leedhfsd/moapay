package com.moa.moapay.global.kafka;

import com.moa.moapay.global.kafkaVo.KafkaMsgVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private static final String TOPIC = "request.payment";
    private final KafkaTemplate<String, Map> kafkaTemplate;

    public void send(Map<String, Object> message, String key) {
        log.info("send message via Map");
        this.kafkaTemplate.send(TOPIC, "1", message);
    }
}
