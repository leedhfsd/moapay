package com.moa.payment.domain.charge.producer;

import com.moa.payment.domain.charge.model.vo.DutchPayCompliteVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
//    private static final String TOPIC = "request.payment";
    private final KafkaTemplate<String, Map> kafkaTemplate;

    public void send(String topic, String key, Map map) {
        log.info("send message via Map");
        this.kafkaTemplate.send(topic, key, map);
    }
}
