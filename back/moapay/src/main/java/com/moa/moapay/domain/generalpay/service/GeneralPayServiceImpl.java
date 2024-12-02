package com.moa.moapay.domain.generalpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.moapay.domain.card.entity.CardProduct;
import com.moa.moapay.domain.card.entity.MyCard;
import com.moa.moapay.domain.card.repository.MyCardQueryRepository;
import com.moa.moapay.domain.card.repository.MyCardRepository;
import com.moa.moapay.domain.card.service.RecommendCardService;
import com.moa.moapay.domain.code.model.dto.GetBarcodeInfoResponseDto;
import com.moa.moapay.domain.code.service.CodeService;
import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteDutchPayRequestDto;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteGeneralPayRequestDto;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteOfflinePayRequestDto;
import com.moa.moapay.domain.generalpay.model.vo.ExecutePaymentRequestVO;
import com.moa.moapay.domain.generalpay.model.vo.PaymentCardInfoVO;
import com.moa.moapay.domain.generalpay.repository.GeneralPayRedisRepository;
import com.moa.moapay.global.exception.BusinessException;
import com.moa.moapay.global.kafka.DutchKafkaProducer;
import com.moa.moapay.global.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralPayServiceImpl implements GeneralPayService {

    private final ObjectMapper objectMapper;
    private final KafkaProducer kafkaProducer;
    private final MyCardRepository myCardRepository;
    private final MyCardQueryRepository myCardQueryRepository;
    private final GeneralPayRedisRepository generalPayRedisRepository;
    private final CodeService codeService;
    private final DutchKafkaProducer dutchKafkaProducer;
    private final RecommendCardService recommendCardService;

    @Override
    public void executeGeneralPay(ExecuteGeneralPayRequestDto dto) {
        // [0] 중복 요청인지 확인
        // 클라이언트가 생성한 requestId가 이미 redis에 저장되었는지를 확인한다
        if (!generalPayRedisRepository.registRequestId(dto.getRequestId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "중복된 결제 요청입니다.");
        }

        // [1] 사용할 카드 선택
        // FIX인 경우, 주어진 값을 기반으로 사용할 카드 값을 가져온다.
        List<PaymentCardInfoVO> cardInfoList = new ArrayList<>();
        if (dto.getCardSelectionType() == CardSelectionType.FIX || dto.getCardSelectionType() == CardSelectionType.DUTCHPAY) {
            // 고정된 값인 경우, cardNumber와 cvc를 기반으로 검증
            // 우선 기본 검증 시행
            // 나중에 유저 본인이 소유한 카드인지 확인하는 매커니즘도 필요하지 않을까...
            MyCard myCard = myCardQueryRepository.findByCardNumberFetchJoin(dto.getCardNumber());

            log.info("moapay 결제요청중 카드 정보 : {}", dto.toString());

            if (myCard == null || !myCard.getCvc().equals(dto.getCvc())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
            }
            CardProduct cardProduct = myCard.getCardProduct();
            // 카드 ID 정보를 포함해 카드 정보 삽입
            // 갱신 : 프론트에 결과를 보낼 때 필요할 정보들 맞춰서 보내기
            PaymentCardInfoVO cardInfoVo = PaymentCardInfoVO.builder()
                    .cardId(myCard.getUuid())
                    .cardName(cardProduct.getName())
                    .imageUrl(cardProduct.getImageUrl())
                    .cardNumber(dto.getCardNumber())
                    .cvc(dto.getCvc())
                    .amount(dto.getTotalPrice()) // 전부 하나의 카드로 긁으므로 totalPrice와 동일
                    .usedAmount(myCard.getAmount())
                    .performance(cardProduct.getPerformance())
                    .benefitUsage(myCard.getBenefitUsage())
                    .build();
            cardInfoList.add(cardInfoVo);
        } else if(dto.getCardSelectionType() == CardSelectionType.RECOMMEND) {
            // 카드 추천형인 경우, 추천된 결과를 기반으로 결제를 진행
            log.info("recommend card...");
            cardInfoList = recommendCardService.recommendPayCard(dto.getMemberId(), dto.getCategoryId(), dto.getRecommendType(), dto.getTotalPrice());
            log.info("recommend done");
            if (cardInfoList.isEmpty()) { // 추천한 결과가 전혀 없다면, 전송받은 주 카드를 이용해 FIX 형식 결제를 시행
                log.info("recommend card is empty : use main card...");
                MyCard myCard = myCardQueryRepository.findByCardNumberFetchJoin(dto.getCardNumber());
                if (myCard == null || !myCard.getCvc().equals(dto.getCvc())) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
                }
                CardProduct cardProduct = myCard.getCardProduct();
                cardInfoList.add(
                        PaymentCardInfoVO.builder()
                                .cardId(myCard.getUuid())
                                .cardName(cardProduct.getName())
                                .imageUrl(cardProduct.getImageUrl())
                                .cardNumber(dto.getCardNumber())
                                .cvc(dto.getCvc())
                                .amount(dto.getTotalPrice()) // 전부 하나의 카드로 긁으므로 totalPrice와 동일
                                .usedAmount(myCard.getAmount())
                                .performance(cardProduct.getPerformance())
                                .benefitUsage(myCard.getBenefitUsage())
                                .build()
                );
            }
            log.info("--- result ---");
            for (PaymentCardInfoVO vo : cardInfoList) {
                log.info(vo.toString());
            }
        }

        // [3] 요청 전송
        ExecutePaymentRequestVO requestVo = ExecutePaymentRequestVO.builder()
                .requestId(dto.getRequestId())
                .orderId(dto.getOrderId())
                .merchantId(dto.getMerchantId())
                .paymentInfoList(cardInfoList)
                .paymentType(String.valueOf(dto.getCardSelectionType()))
                .operation("PROGRESS")
                .build();
        Map<String, Object> map = objectMapper.convertValue(requestVo, Map.class);
        kafkaProducer.send(map, "1");
    }

    @Override
    public void executeDutchPay(ExecuteDutchPayRequestDto dto) {
        // [0] 중복 요청인지 확인
        // 클라이언트가 생성한 requestId가 이미 redis에 저장되었는지를 확인한다
        if (!generalPayRedisRepository.registRequestId(dto.getRequestId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "중복된 결제 요청입니다.");
        }

        log.info("결제 dto 정보 = {}", dto.toString());

        // [1] 사용할 카드 선택
        // FIX인 경우, 주어진 값을 기반으로 사용할 카드 값을 가져온다.
        List<PaymentCardInfoVO> cardInfoList = new ArrayList<>();
        if (dto.getCardSelectionType() == CardSelectionType.DUTCHPAY) {
            // 고정된 값인 경우, cardNumber와 cvc를 기반으로 검증
            // 우선 기본 검증 시행
            // 나중에 유저 본인이 소유한 카드인지 확인하는 매커니즘도 필요하지 않을까...
            MyCard myCard = myCardQueryRepository.findByCardNumberFetchJoin(dto.getCardNumber());

            log.info("moapay 결제요청중 카드 정보 : {}", dto.toString());

            if (myCard == null || !myCard.getCvc().equals(dto.getCvc())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
            }
            CardProduct cardProduct = myCard.getCardProduct();
            // 카드 ID 정보를 포함해 카드 정보 삽입
            // 갱신 : 프론트에 결과를 보낼 때 필요할 정보들 맞춰서 보내기
            PaymentCardInfoVO cardInfoVo = PaymentCardInfoVO.builder()
                    .cardId(myCard.getUuid())
                    .cardName(cardProduct.getName())
                    .imageUrl(cardProduct.getImageUrl())
                    .cardNumber(dto.getCardNumber())
                    .cvc(dto.getCvc())
                    .amount(dto.getTotalPrice()) // 전부 하나의 카드로 긁으므로 totalPrice와 동일
                    .usedAmount(myCard.getAmount())
                    .performance(cardProduct.getPerformance())
                    .benefitUsage(myCard.getBenefitUsage())
                    .build();
            cardInfoList.add(cardInfoVo);
        }

        // [3] 요청 전송
        ExecutePaymentRequestVO requestVo = ExecutePaymentRequestVO.builder()
                .dutchPayId(dto.getDutchPayId())
                .requestId(dto.getRequestId())
                .orderId(dto.getOrderId())
                .merchantId(dto.getMerchantId())
                .paymentInfoList(cardInfoList)
                .paymentType(String.valueOf(dto.getCardSelectionType()))
                .operation("PROGRESS")
                .build();
        Map<String, Object> map = objectMapper.convertValue(requestVo, Map.class);
        kafkaProducer.send(map, "1");
    }

    @Override
    public void executeOfflinePay(ExecuteOfflinePayRequestDto dto) {
        // [0] 중복 요청인지 확인
        // 클라이언트가 생성한 requestId가 이미 redis에 저장되었는지를 확인한다
        if (!generalPayRedisRepository.registRequestId(dto.getRequestId())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "중복된 결제 요청입니다.");
        }
        // 바코드를 기반으로 결제에 필요한 카드 정보 가져오기
        GetBarcodeInfoResponseDto barcodeInfo = codeService.getBarcodeInfo(dto.getBarcode());

        // [1] 사용할 카드 선택
        // FIX인 경우, 주어진 값을 기반으로 사용할 카드 값을 가져온다.
        List<PaymentCardInfoVO> cardInfoList = new ArrayList<>();
        if (barcodeInfo.getType() == CardSelectionType.FIX) {
            // 바코드를 등록할 때 검증을 시행했으므로 별도의 검증을 거치지 않음
//            MyCard myCard = myCardRepository.findByCardNumber(barcodeInfo.getCardNumber())
//                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
            // 카드 ID 정보를 포함해 카드 정보 삽입
            PaymentCardInfoVO cardInfoVo = PaymentCardInfoVO.builder()
                    .cardId(myCardQueryRepository.findUuidByCardNumber(barcodeInfo.getCardNumber()))
                    .cardNumber(barcodeInfo.getCardNumber())
                    .cvc(barcodeInfo.getCvc())
                    .amount(dto.getTotalPrice()) // 전부 하나의 카드로 긁으므로 totalPrice와 동일
                    .build();
            cardInfoList.add(cardInfoVo);

        } else {
            // 카드 추천형인 경우, 추천된 결과를 기반으로 결제를 진행
            // todo : 추천형인 경우 바코드에 더 많은 정보를 담도록 변경
            log.info("recommend card...");
//            cardInfoList = recommendCardService.recommendPayCard(dto.getMemberId(), dto.getCategoryId(), dto.getRecommendType(), dto.getTotalPrice());
            log.info("recommend done");
            return;
        }

        // [3] 요청 전송
        ExecutePaymentRequestVO requestVo = ExecutePaymentRequestVO.builder()
                .requestId(dto.getRequestId())
                .orderId(dto.getOrderId())
                .merchantId(dto.getMerchantId())
                .paymentInfoList(cardInfoList)
                .operation("PROGRESS")
                .build();
        Map<String, Object> map = objectMapper.convertValue(requestVo, Map.class);
        kafkaProducer.send(map, "1");
    }

    @Override
    public void dutchCancel(UUID paymentId) {
        Map<String, Object> map = new HashMap<>();
        map.put("paymentId", paymentId);
        dutchKafkaProducer.send(map, "2");
    }


}
