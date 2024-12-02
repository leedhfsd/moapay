package com.moa.payment.domain.charge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.payment.domain.charge.entity.PaymentLog;
import com.moa.payment.domain.charge.model.PayStatus;
import com.moa.payment.domain.charge.model.PaymentResultStatus;
import com.moa.payment.domain.charge.model.ProcessingStatus;
import com.moa.payment.domain.charge.model.dto.*;
import com.moa.payment.domain.charge.model.vo.*;
import com.moa.payment.domain.charge.producer.KafkaProducer;
import com.moa.payment.domain.charge.repository.PaymentLogRepository;
import com.moa.payment.domain.saving.service.SavingService;
import com.moa.payment.global.exception.BusinessException;
import com.moa.payment.global.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargeServiceImpl implements ChargeService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final PaymentLogRepository paymentLogRepository;
    //    private final KafkaTemplate<String, DutchPayCompliteVo> kafkaTemplate;
    private final KafkaProducer kafkaProducer;
    private final SavingService savingService;
    @Value("${external-url.cardbank}")
    private String cardbankUrl;
    @Value("${external-url.store}")
    private String storeUrl;

    @Override
    public ExecutePaymentResultVO executePayment(ExecutePaymentRequestVO vo) {
        log.info("ExcutePaymentRequestVO : {}", vo.toString());
        List<PaymentCardInfoVO> paymentInfoList = vo.getPaymentInfoList();
        List<PaymentCardInfoVO> succeedPaymentInfoList = new ArrayList<>();
        List<UUID> succeedPaymentIdList = new ArrayList<>();
        List<PaymentResultCardInfoVO> paymentResultInfoList = new ArrayList<>();
        String merchantName = "";
        for (PaymentCardInfoVO paymentInfo : paymentInfoList) {
            CardPaymentRequestDto paymentRequestDto = CardPaymentRequestDto.builder()
                    .merchantId(vo.getMerchantId())
                    .cardId(paymentInfo.getCardId())
                    .cardNumber(paymentInfo.getCardNumber())
                    .cvc(paymentInfo.getCvc())
                    .amount(paymentInfo.getAmount())
                    .build();
            ResponseEntity<Map> paymentResponse = restClient.post()
                    .uri(cardbankUrl + "/card/pay")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequestDto)
                    .retrieve()
                    .toEntity(Map.class);
            CardPaymentResponseDto paymentResponseDto = objectMapper.convertValue(paymentResponse.getBody().get("data"), CardPaymentResponseDto.class);
            merchantName = paymentResponseDto.getMerchantName();
            // 만일 결제에 실패했다면 결제 취소 과정을 거쳐야 한다
            if (!paymentResponse.getStatusCode().is2xxSuccessful() || paymentResponseDto.getStatus() != PayStatus.APPROVED) {
                // 결제 중 오류가 발생했거나, 한도초과이거나, 잔액부족으로 결제가 실패했을 경우 결제를 중지한다
                log.info("payment failed - status : {}", paymentResponseDto.getStatus());
                int successSize = succeedPaymentInfoList.size();
                for (int s = 0; s < successSize; ++s) {
                    PaymentCardInfoVO cardInfo = succeedPaymentInfoList.get(s);
                    UUID paymentId = succeedPaymentIdList.get(s);
                    // 이 값들을 기반으로 결제 취소 요청을 보낸다
                    CancelPayRequestDto requestDto = CancelPayRequestDto.builder()
                            .paymentId(paymentId)
                            .cardId(cardInfo.getCardId())
                            .cardNumber(cardInfo.getCardNumber())
                            .cvc(cardInfo.getCvc())
                            .build();
                    CancelPayment(requestDto);
                }

                if (vo.getPaymentType().equals("DUTCHPAY")) {
                    log.info("더치페이 요청 캔슬");
                    DutchPayCompliteVo dutchPayCompliteVo = DutchPayCompliteVo.builder()
                            .requestId(vo.getRequestId())
                            .paymentType(vo.getPaymentType())
                            .paymentInfoList(vo.getPaymentInfoList())
                            .orderId(vo.getOrderId())
                            .merchantId(vo.getMerchantId())
                            .dutchUuid(vo.getDutchPayId())
                            .status("PROGRESS")
                            .build();

                    Map<String, Object> map = new HashMap<>();
                    map.put("dutchpayList", dutchPayCompliteVo);

                    kafkaProducer.send("tracking.dutchpay", "2", map);
                }
                // 결제 실패 관련 처리가 끝났다면 더이상 결제를 진행하지 않음
                return ExecutePaymentResultVO.builder()
                        .merchantName(paymentResponseDto.getMerchantName())
                        .status(PaymentResultStatus.FAILED)
                        .build();
            }
            // 결제에 성공한 경우, succeedList에 넣음
            log.info(paymentResponseDto.toString());
            succeedPaymentInfoList.add(paymentInfo);
            log.info("succeed payment ID : {}", paymentResponseDto.getPaymentId());
            succeedPaymentIdList.add(paymentResponseDto.getPaymentId());
            // 이후 save 시도
            PaymentLog paymentLog = PaymentLog.builder()
                    .uuid(paymentResponseDto.getPaymentId()) // 카드사쪽 결제로그와 이쪽 결제로그의 uuid를 통일
                    .cardId(paymentInfo.getCardId())
                    .amount(paymentResponseDto.getAmount())
                    .status(ProcessingStatus.APPROVED)
                    .cardNumber(paymentInfo.getCardNumber())
                    .cvc(paymentInfo.getCvc())
                    .merchantId(vo.getMerchantId())
                    .merchantName(paymentResponseDto.getMerchantName())
                    .categoryId(paymentResponseDto.getCategoryId())
                    .benefitBalance(paymentResponseDto.getBenefitBalance())
                    .build();
            System.out.println("paymentLog 저장 성공");

            PaymentLog savedPaymentLog = paymentLogRepository.save(paymentLog);
            // paymentLog 저장 후 updateTodayAmount 호출
//            savingService.updateTodayAmount(savedPaymentLog);
            // 저장에도 성공했다면 성공 리스트에 넣는다
            paymentResultInfoList.add(
                    PaymentResultCardInfoVO.builder()
                            .paymentId(paymentResponseDto.getPaymentId())
                            .cardId(paymentInfo.getCardId())
                            .cardNumber(paymentInfo.getCardNumber())
                            .amount(paymentInfo.getAmount())
                            .actualAmount(paymentResponseDto.getAmount())
                            .benefitActivated(paymentResponseDto.isBenefitActivated())
                            .benefitBalance(paymentResponseDto.getBenefitBalance())
                            .remainedBenefit(paymentResponseDto.getRemainedBenefit())
                            .benefitDetail(paymentResponseDto.getBenefitDetail())
                            .build()
            );
        }
        // 전부 결제에 성공했다면 성공 로그 발송
        // 궁금하니까 로그 찍어보기...
        log.info("pay succeeded : {}", merchantName);
//        for(PaymentResultCardInfoVO v : paymentResultInfoList) {
//            log.info(v.toString());
//        }
        if (vo.getPaymentType().equals("DUTCHPAY")) {
            log.info("더치페이 요청");
            DutchPayCompliteVo dutchPayCompliteVo = DutchPayCompliteVo.builder()
                    .requestId(vo.getRequestId())
                    .paymentType(vo.getPaymentType())
                    .paymentInfoList(vo.getPaymentInfoList())
                    .orderId(vo.getOrderId())
                    .merchantId(vo.getMerchantId())
                    .dutchUuid(vo.getDutchPayId())
                    .status("DONE")
                    .build();

            Map<String, Object> map = new HashMap<>();
            map.put("dutchpayList", dutchPayCompliteVo);

            kafkaProducer.send("tracking.dutchpay", "2", map);
        }
        return ExecutePaymentResultVO.builder()
                .merchantName(merchantName)
                .status(PaymentResultStatus.SUCCEED)
                .paymentResultInfoList(paymentResultInfoList)
                .build();
    }

    @Override
    public void CancelPayment(CancelPayRequestDto dto) {
        ResponseEntity<Map> cancelResponse = restClient.post()
                .uri(cardbankUrl + "/card/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .toEntity(Map.class);
        if (!cancelResponse.getStatusCode().is2xxSuccessful()) {
            // 결제 취소가 잘 되지 않았다면, 그 내용을 기반으로 exception을 발생시킨다.
            ErrorResponse errorResponse = objectMapper.convertValue(cancelResponse.getBody().get("data"), ErrorResponse.class);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "결제 취소 실패 : " + errorResponse.getMessage());
        }
        CancelPayResponseDto responseDto = objectMapper.convertValue(cancelResponse.getBody().get("data"), CancelPayResponseDto.class);
        log.info("canceled - amount : {}, benefitBalance : {}, remainedBenefit : {}", responseDto.getAmount(), responseDto.getBenefitBalance(), responseDto.getRemainedBenefit());
        // 취소 처리가 되었다면, 로컬의 payment log status도 바꿔야 함
        PaymentLog paymentLog = paymentLogRepository.findByUuid(dto.getPaymentId()).get();
        PaymentLog newLog = paymentLog.toBuilder()
                .status(ProcessingStatus.CANCELED)
                .build();
        paymentLogRepository.save(newLog);
    }

    @Override
    public PaymentResultDto makePaymentResultDto(ExecutePaymentResultVO resultVo, ExecutePaymentRequestVO requestVO) {
        log.info("making PaymentResultDto...");
        if (resultVo.getStatus() == PaymentResultStatus.FAILED) {
            // 실패한 경우, 최소한의 데이터만 전송
            return PaymentResultDto.builder()
                    .status(PaymentResultStatus.FAILED)
                    .requestId(requestVO.getRequestId())
                    .merchantName(resultVo.getMerchantName())
                    .build();
        }
        long totalAmount = 0;
        int usedCardCount = 0;
        List<PaymentResultCardInfoDto> paymentResultCardInfoList = new ArrayList<>();
        for (int i = 0; i < resultVo.getPaymentResultInfoList().size(); ++i) {
            PaymentCardInfoVO requestCardInfo = requestVO.getPaymentInfoList().get(i);
            PaymentResultCardInfoVO resultCardInfo = resultVo.getPaymentResultInfoList().get(i);
            if (!requestCardInfo.getCardId().equals(resultCardInfo.getCardId())) {
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "카드 정보 불일치!!!");
            }
            totalAmount += resultCardInfo.getActualAmount();
            usedCardCount++;
            paymentResultCardInfoList.add(
                    PaymentResultCardInfoDto.builder()
                            .paymentId(resultCardInfo.getPaymentId())
                            .cardName(requestCardInfo.getCardName())
                            .imageUrl(requestCardInfo.getImageUrl())
                            .cardId(requestCardInfo.getCardId())
                            .cardNumber(requestCardInfo.getCardNumber())
                            .amount(requestCardInfo.getAmount())
                            .actualAmount(resultCardInfo.getActualAmount())
                            .performance(requestCardInfo.getPerformance())
                            .usedAmount(requestCardInfo.getUsedAmount() + resultCardInfo.getActualAmount())
                            .benefitActivated(resultCardInfo.isBenefitActivated())
                            .benefitUsage(requestCardInfo.getBenefitUsage() + resultCardInfo.getBenefitBalance())
                            .benefitDetail(resultCardInfo.getBenefitDetail())
                            .build()
            );
        }
        return PaymentResultDto.builder()
                .status(PaymentResultStatus.SUCCEED)
                .requestId(requestVO.getRequestId())
                .merchantName(resultVo.getMerchantName())
                .totalAmount(totalAmount)
                .createTime(LocalDateTime.now())
                .usedCardCount(usedCardCount)
                .paymentResultCardInfoList(paymentResultCardInfoList)
                .build();
    }

    @Override
    public void sendResultToStore(UUID orderId, ExecutePaymentResultVO vo) {
        log.info("send result to store : {}", vo.getMerchantName());
        List<StoreResultDto> paymentInfo = new ArrayList<>();
        for (PaymentResultCardInfoVO resultVo : vo.getPaymentResultInfoList()) {
            paymentInfo.add(
                    StoreResultDto.builder()
                            .cardNumber(resultVo.getCardNumber())
                            .amount(resultVo.getAmount())
                            .actualAmount(resultVo.getActualAmount())
                            .build()
            );
        }
        SendResultToStoreRequestDto dto = SendResultToStoreRequestDto.builder()
                .orderId(orderId)
                .paymentInfo(paymentInfo)
                .build();
        try {
            ResponseEntity<Map> res = restClient.post()
                    .uri(storeUrl + "/payment/online/result")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .toEntity(Map.class);
        } catch (ResourceAccessException e) {
            log.error("send result to store error");
        }
    }

    @Override
    public void dutchCancel(UUID paymentId) {
        log.info("dutchCancel");
        Optional<PaymentLog> paymentLogOptional = paymentLogRepository.findByUuid(paymentId);

        if (paymentLogOptional.isPresent()) {

            log.info(paymentLogOptional.get().getCardId().toString());

            CancelPayRequestDto requestDto = CancelPayRequestDto.builder()
                    .paymentId(paymentId)
                    .cardId(paymentLogOptional.get().getCardId())
                    .cardNumber(paymentLogOptional.get().getCardNumber())
                    .cvc(paymentLogOptional.get().getCvc())
                    .build();
            ResponseEntity<Map> cancelResponse = restClient.post()
                    .uri(cardbankUrl + "/card/cancel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .toEntity(Map.class);

        } else {
            throw new BusinessException(HttpStatus.NOT_FOUND, "PaymentLog not found for paymentId: " + paymentId);
        }
    }

    @Override
    public GetPaymentLogResponseDto getPayMentLog(GetPaymentLogRequestDto dto) {

        List<UUID> uuidList = dto.getCardId();

        // 카테고리 ID와 카운트를 저장할 맵 초기화
        Map<String, Long> categoryCountMap = new HashMap<>();

        // 카테고리 ID와 해당하는 인덱스를 초기화
        for (long i = 0; i <= 20; i++) {
            String key = String.format("C%04d", i);  // C0000, C0001, ..., C0020 형식으로 만듦
            categoryCountMap.put(key, 0L);
        }

        // 모든 UUID에 대해 반복
        for (UUID uuid : uuidList) {
            List<Object[]> objects = paymentLogRepository.countByCategoryIdGroupedByCardId(uuid);

            for (Object[] object : objects) {
                String categoryId = (String) object[0];
                Long count = (Long) object[1];

                log.info("categoryId : {}, count : {}", categoryId, count);

                // categoryId에 해당하는 카운트를 업데이트
                if (categoryCountMap.containsKey(categoryId)) {
                    // 현재 카운트에 추가
                    categoryCountMap.put(categoryId, categoryCountMap.get(categoryId) + count);
                }
            }
        }

        GetPaymentLogResponseDto getPaymentLogResponseDto = GetPaymentLogResponseDto.builder().categoryCountMap(categoryCountMap).build();

        return getPaymentLogResponseDto;
    }

}
