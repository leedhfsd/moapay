package com.moa.moapay.domain.card.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.moapay.domain.card.model.vo.PaymentResultCardInfoVO;
import com.moa.moapay.domain.card.service.MyCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardListener {

    private final ObjectMapper objectMapper;
    private final MyCardService myCardService;

    @KafkaListener(topics = "request.renew-card-info", groupId = "card_consumer_group")
    public void renewCardInfo(String message) throws JsonProcessingException {
        Map<String, Object> vo = objectMapper.readValue(message, Map.class);
        log.info("received renew card info message");
        List<LinkedHashMap<String, Object>> receivedList = (List<LinkedHashMap<String, Object>>) vo.get("renewList");
        List<PaymentResultCardInfoVO> renewList = new ArrayList<>();
        for(LinkedHashMap<String, Object> info : receivedList) {
            renewList.add(objectMapper.convertValue(info, PaymentResultCardInfoVO.class));
        }
        myCardService.renewCardInfo(renewList);
    }
}
