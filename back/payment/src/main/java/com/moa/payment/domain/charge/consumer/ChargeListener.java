package com.moa.payment.domain.charge.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.charge.model.PaymentResultStatus;
import com.moa.payment.domain.charge.model.dto.PaymentResultDto;
import com.moa.payment.domain.charge.model.vo.*;
import com.moa.payment.domain.charge.producer.KafkaProducer;
import com.moa.payment.domain.charge.repository.ChargeRedisRepository;
import com.moa.payment.domain.charge.service.ChargeService;
import com.moa.payment.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargeListener {

    private final ChargeService chargeService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Map> kafkaTemplate;
    private final KafkaProducer kafkaProducer;

    @KafkaListener(topics = "request.payment", groupId = "payments_consumer_group")
    public void executePayment(String message) {
        try {
            Map<String, Object> vo = objectMapper.readValue(message, Map.class);
            ExecutePaymentRequestVO executePaymentRequestVO = objectMapper.convertValue(vo, ExecutePaymentRequestVO.class);
            log.info("get payment request : {}", executePaymentRequestVO.getDutchPayId());
            ExecutePaymentResultVO resultVO = chargeService.executePayment(executePaymentRequestVO);
            if (resultVO.getStatus() == PaymentResultStatus.SUCCEED) {
                log.info("transfer payment result...");
                // 결제가 완료되었으므로, 결제 관련 데이터 갱신 요청을 보내야 함
                List<PaymentResultCardInfoVO> renewList = resultVO.getPaymentResultInfoList();
                Map<String, Object> map = new HashMap<>();
                map.put("renewList", renewList);
                kafkaTemplate.send("request.renew-card-info", "1", map);
                // 이어서 결제 결과를 가맹점에 전달
                //TODO: 가맹점 헬프
                try {
                    chargeService.sendResultToStore(executePaymentRequestVO.getOrderId(), resultVO);
                } catch (Exception e) {
                    log.error("failed to send info to store");
                }
            } else {
                List<PaymentResultCardInfoVO> renewList = resultVO.getPaymentResultInfoList();
                log.info("더치페이 요청 캔슬");
                DutchPayCompliteVo dutchPayCompliteVo = DutchPayCompliteVo.builder()
                        .requestId(executePaymentRequestVO.getRequestId())
                        .dutchUuid(executePaymentRequestVO.getDutchPayId())
                        .status("PROGRESS")
                        .build();
                Map<String, Object> map = new HashMap<>();
                map.put("dutchpayList", dutchPayCompliteVo);
                kafkaProducer.send("tracking.dutchpay","2", map);
            }
            // 마지막으로 client에게 응답을 전송
            PaymentResultDto resultDto = chargeService.makePaymentResultDto(resultVO, executePaymentRequestVO);
            notificationService.sendCompleteMessage(executePaymentRequestVO.getRequestId(), resultDto);
        } catch (JsonProcessingException e) {
            log.info("failed to parse json object");
        }
    }

}
