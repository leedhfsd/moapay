package com.moa.moapay.domain.card.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.moapay.domain.card.entity.CardBenefit;
import com.moa.moapay.domain.card.entity.CardProduct;
import com.moa.moapay.domain.card.entity.MyCard;
import com.moa.moapay.domain.card.model.dto.*;
import com.moa.moapay.domain.card.model.dto.getMyCard.GetMyCardDto;
import com.moa.moapay.domain.card.model.dto.getMyCard.GetMyCardDtoWrapper;
import com.moa.moapay.domain.card.model.vo.PaymentResultCardInfoVO;
import com.moa.moapay.domain.card.repository.CardBenefitRepository;
import com.moa.moapay.domain.card.repository.CardProductRepository;
import com.moa.moapay.domain.card.repository.MyCardQueryRepository;
import com.moa.moapay.domain.card.repository.MyCardRepository;
import com.moa.moapay.global.exception.BusinessException;
import com.moa.moapay.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCardServiceImpl implements MyCardService {

    private final CardProductRepository cardProductRepository;
    private final MyCardQueryRepository myCardQueryRepository;
    private final RestClient restClient;
    private final MyCardRepository myCardRepository;
    private final CardBenefitRepository cardBenefitRepository;
    private final ObjectMapper objectMapper;
    @Value("${external-url.cardbank}")
    private String cardbankUrl;
    @Value("${external-url.payment}")
    private String paymentUrl;

    @Override
    public List<GetMyCardsResponseDto> getMyCardInfo(UUID memberId) {
        // todo: 여기서 쿠키를 뜯어서 UUID 찾기
        // 이건 테스트용

        List<MyCard> myCards = myCardQueryRepository.findAllByMemberIdWithBenefits(memberId);

        if (myCards.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "등록된 카드가 없어요");
        }

        List<GetMyCardsResponseDto> myCardsDto = myCards.stream().map(myCard -> {
            List<CardBenefitDto> benefitDtos = myCard.getCardProduct()
                    .getBenefits()
                    .stream()
                    .map(benefit -> CardBenefitDto.builder()
                            .categoryName(benefit.getCardBenefitCategory().getName())
                            .benefitType(benefit.getBenefitType())
                            .benefitUnit(benefit.getBenefitUnit())
                            .benefitValue(benefit.getBenefitValue())
                            .benefitDesc(benefit.getBenefitDesc())
                            .benefitPoint(benefit.getBenefitPoint())
                            .build())
                    .collect(Collectors.toList());

            CardProductDto cardInfo = CardProductDto.builder()
                    .cardBenefits(benefitDtos)
                    .cardProductPerformance(myCard.getCardProduct().getPerformance())
                    .cardProductName(myCard.getCardProduct().getName())
                    .cardProductUuid(myCard.getCardProduct().getUuid())
                    .cardProductType(myCard.getCardProduct().getType().toString())
                    .cardProductCompanyName(myCard.getCardProduct().getCompanyName())
                    .cardProductBenefitTotalLimit(myCard.getCardProduct().getBenefitTotalLimit())
                    .cardProductImgUrl(myCard.getCardProduct().getImageUrl())
                    .cardProductAnnualFee(myCard.getCardProduct().getAnnualFee())
                    .cardProductAnnualFeeForeign(myCard.getCardProduct().getAnnualFeeForeign())
                    .build();


            return GetMyCardsResponseDto.builder()
                    .cardProduct(cardInfo)
                    .uuid(myCard.getUuid())
                    .cardLimit(myCard.getCardLimit())
                    .cvc(myCard.getCvc())
                    .amount(myCard.getAmount())
                    .benefitUsage(myCard.getBenefitUsage())
                    .performanceFlag(myCard.isPerformanceFlag())
                    .cardNumber(String.valueOf(myCard.getCardNumber()))
                    .build();
        }).collect(Collectors.toList());

        log.info("myCards size {}", myCards.size());
        return myCardsDto;
    }

    @Override
    public List<CardInfoResponseDto> getAllCard() {

        List<CardProduct> cards = myCardQueryRepository.findAll();

        List<CardInfoResponseDto> cardsDto = cards.stream().map(cardProduct -> {
            List<CardBenefitDto> benefitDtos = cardProduct.getBenefits()
                    .stream()
                    .map(benefit -> CardBenefitDto.builder()
                            .categoryName(benefit.getCardBenefitCategory().getName())
                            .benefitUnit(benefit.getBenefitUnit())
                            .benefitDesc(benefit.getBenefitDesc())
                            .benefitValue(benefit.getBenefitValue())
                            .benefitType(benefit.getBenefitType())
                            .benefitPoint(benefit.getBenefitPoint())
                            .build())
                    .collect(Collectors.toList());

            return CardInfoResponseDto.builder()
                    .cardName(cardProduct.getName())
                    .cardType(cardProduct.getType())
                    .annualFee(cardProduct.getAnnualFee())
                    .annualFeeForeign(cardProduct.getAnnualFeeForeign())
                    .performance(cardProduct.getPerformance())
                    .companyName(cardProduct.getCompanyName())
                    .benefitTotalLimit(cardProduct.getBenefitTotalLimit())
                    .imageUrl(cardProduct.getImageUrl())
                    .benefits(benefitDtos)
                    .build();
        }).collect(Collectors.toList());

        return cardsDto;
    }

    @Override
    public void renewCardInfo(List<PaymentResultCardInfoVO> renewList) {
        for (PaymentResultCardInfoVO vo : renewList) {
            // 맞지 않는 부분이 있다면, 현재 값을 기준으로 갱신해주는 게 맞을 것 같긴 한데...
            MyCard myCard = myCardRepository.findByUuid(vo.getCardId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다."));
            log.info("found myCard : {}", myCard.getUuid());
            long amount = myCard.getAmount();
            long benefitUsage = myCard.getBenefitUsage();
            MyCard newCard = myCard.toBuilder()
                    .performanceFlag(vo.isBenefitActivated())
                    .amount(amount + vo.getAmount())
                    .benefitUsage(benefitUsage + vo.getBenefitBalance())
                    .build();
            myCardRepository.save(newCard);
        }
    }

    @Override
    @Transactional
    public List<GetMyCardsResponseDto> getMyCardFromCardBank(GetMyCardsRequestDto getMyCardsRequestDto) {
        // 카드 뱅크에서 내 카드 목록을 가져오기 위한 REST API 호출
        ResponseEntity<GetMyCardDtoWrapper> responseEntity = restClient.post()
                .uri(cardbankUrl + "/card/getMyCards")
                .contentType(MediaType.APPLICATION_JSON)
                .body(getMyCardsRequestDto)
                .retrieve()
                .toEntity(GetMyCardDtoWrapper.class);

        // 응답 상태 코드 확인
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            List<GetMyCardDto> response = responseEntity.getBody().getData();
            List<GetMyCardsResponseDto> getMyCardsResponseDtos = response.stream().map(card -> {

                List<CardBenefit> cardBenefit = cardBenefitRepository.findByCardProductUuid(
                        card.getCardProduct().getCardProductUuid());

                List<CardBenefitDto> cardBenefitDtos = cardBenefit.stream().map(
                        cardBenefit1 -> {
                            return CardBenefitDto.builder()
                                    .benefitDesc(cardBenefit1.getBenefitDesc())
                                    .benefitType(cardBenefit1.getBenefitType())
                                    .benefitValue(cardBenefit1.getBenefitValue())
                                    .benefitUnit(cardBenefit1.getBenefitUnit())
                                    .categoryName(cardBenefit1.getCardBenefitCategory().getName())
                                    .benefitPoint(cardBenefit1.getBenefitPoint())
                                    .build();
                        }
                ).toList();

                CardProductDto cardProductDto = CardProductDto.builder()
                        .cardProductUuid(card.getCardProduct().getCardProductUuid())
                        .cardProductName(card.getCardProduct().getCardProductName())
                        .cardProductCompanyName(card.getCardProduct().getCardProductCompanyName())
                        .cardProductBenefitTotalLimit(card.getCardProduct().getCardProductBenefitTotalLimit())
                        .cardProductType(card.getCardProduct().getCardProductType())
                        .cardProductAnnualFee(card.getCardProduct().getCardProductAnnualFee())
                        .cardProductAnnualFeeForeign(card.getCardProduct().getCardProductAnnualFeeForeign())
                        .cardProductPerformance(card.getCardProduct().getCardProductPerformance())
                        .cardProductImgUrl(card.getCardProduct().getCardProductImgUrl())
                        .cardBenefits(cardBenefitDtos)
                        .build();

                AccountDto accountDto = AccountDto.builder()
                        .accountUuid(card.getAccounts().getAccountUuid())
                        .accountNumber(card.getAccounts().getAccountNumber())
                        .balance(card.getAccounts().getBalance())
                        .build();

                CardProduct cardProduct = cardProductRepository.findByUuid(card.getCardProduct().getCardProductUuid());

                log.info("Member Id : {}", getMyCardsRequestDto.getMemberUuid());

                MyCard myCard = MyCard.builder()
                        .uuid(card.getUuid())
                        .amount(card.getAmount())
                        .benefitUsage(card.getBenefitUsage())
                        .cardLimit(card.getCardLimit())
                        .cardNumber(card.getCardNumber())
                        .cvc(card.getCvc())
                        .memberId(getMyCardsRequestDto.getMemberUuid())
                        .performanceFlag(card.isPerformanceFlag())
                        .cardStatus(true)
                        .cardProduct(cardProduct)
                        .build();

                // 만약 카드사에서 해지하면??
                Optional<MyCard> existMycard = myCardRepository.findByCardNumber(
                        String.valueOf(myCard.getCardNumber()));
                if (existMycard.isPresent()) {
                    return GetMyCardsResponseDto.builder()
                            .uuid(card.getUuid())
                            .cardNumber(card.getCardNumber())
                            .cvc(card.getCvc())
                            .cardLimit(card.getCardLimit())
                            .performanceFlag(card.isPerformanceFlag())
                            .amount(card.getAmount())
                            .benefitUsage(card.getBenefitUsage())
                            .cardProduct(cardProductDto)
                            .account(accountDto)
                            .build();
                } else {
                    myCardRepository.save(myCard);
                    return GetMyCardsResponseDto.builder()
                            .uuid(card.getUuid())
                            .cardNumber(card.getCardNumber())
                            .cvc(card.getCvc())
                            .cardLimit(card.getCardLimit())
                            .performanceFlag(card.isPerformanceFlag())
                            .amount(card.getAmount())
                            .benefitUsage(card.getBenefitUsage())
                            .cardProduct(cardProductDto)
                            .account(accountDto)
                            .build();
                }
            }).toList();
            return getMyCardsResponseDtos;
        }
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러");
    }

    /**
     * 카드 추가
     *
     * @param registrationRequestDto
     * @return
     */
    @Override
    @Transactional
    public GetMyCardsResponseDto registrationCard(CardRegistrationRequestDto registrationRequestDto) {

        List<MyCard> myCards = myCardRepository.findAllByMemberId(registrationRequestDto.getMemberUuid());

        log.info("myCards Size : {}", myCards.size());
        Optional<MyCard> existMycard = myCardRepository.findByCardNumber(String.valueOf(registrationRequestDto.getCardNumber()));
        if (existMycard.isPresent()) {
            log.info("카드가 이미 존재한다");
            throw new BusinessException(HttpStatus.CONFLICT, "카드가 이미 존재합니다");
        }

        try {
            log.info("try문 진입함");
            ResponseEntity<CardRestWrapperDto> responseEntity = restClient.post()
                    .uri(cardbankUrl + "/card/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(registrationRequestDto)
                    .retrieve()
                    .toEntity(CardRestWrapperDto.class);
            log.info("responseEntity = {} ", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                CardRestTemplateDto cardData = responseEntity.getBody().getData();
                log.info("cardData = {} ", cardData);

                // 이제 카드 상품 받았으니 저장해야지
                UUID memberUuid = registrationRequestDto.getMemberUuid();
                CardProduct cardProduct = cardProductRepository.findByUuid(cardData.getCardProductUuid());

                MyCard myCard = MyCard.builder()
                        .uuid(UUID.randomUUID())
                        .cardNumber(cardData.getCardNumber())
                        .cvc(cardData.getCvc())
                        .cardProduct(cardProduct)
                        .performanceFlag(cardData.isPerformanceFlag())
                        .amount(cardData.getAmount())
                        .benefitUsage(cardData.getBenefitUsage())
                        .memberId(memberUuid)
                        .cardLimit(cardData.getCardLimit())
                        .cardStatus(true)
                        .memberId(memberUuid)
                        .build();

                myCardRepository.save(myCard);

                List<CardBenefit> cardBenefits = cardBenefitRepository.findByCardProductUuid(
                        cardData.getCardProductUuid());
                List<CardBenefitDto> cardBenefitDtos = cardBenefits.stream().map(cardBenefit -> {
                    return CardBenefitDto.builder()
                            .benefitPoint(cardBenefit.getBenefitPoint())
                            .benefitType(cardBenefit.getBenefitType())
                            .benefitValue(cardBenefit.getBenefitValue())
                            .benefitUnit(cardBenefit.getBenefitUnit())
                            .benefitDesc(cardBenefit.getBenefitDesc())
                            .categoryName(cardBenefit.getCardBenefitCategory().getName())
                            .build();
                }).toList();

                CardProductDto cardProductDto = CardProductDto.builder()
                        .cardProductUuid(cardData.getCardProductUuid())
                        .cardProductName(cardData.getCardProductName())
                        .cardProductCompanyName(cardData.getCardProductCompanyName())
                        .cardProductAnnualFee(cardData.getCardProductAnnualFee())
                        .cardProductAnnualFeeForeign(cardData.getCardProductAnnualFeeForeign())
                        .cardProductType(cardData.getCardProductType())
                        .cardProductImgUrl(cardData.getCardProductImgUrl())
                        .cardProductBenefitTotalLimit(cardData.getCardProductBenefitTotalLimit())
                        .cardProductPerformance(cardData.getCardProductPerformance())
                        .cardBenefits(cardBenefitDtos)
                        .build();

                AccountDto accountDto = AccountDto.builder()
                        .accountUuid(cardData.getAccountUuid())
                        .accountNumber(cardData.getAccountNumber())
                        .balance(cardData.getBalance())
                        .build();

                GetMyCardsResponseDto getMyCardsResponseDto = GetMyCardsResponseDto.builder()
                        .uuid(myCard.getUuid())
                        .cardNumber(cardData.getCardNumber())
                        .cvc(cardData.getCvc())
                        .performanceFlag(cardData.isPerformanceFlag())
                        .cardLimit(cardData.getCardLimit())
                        .amount(cardData.getAmount())
                        .benefitUsage(cardData.getBenefitUsage())
                        .cardProduct(cardProductDto)
                        .account(accountDto)
                        .build();

                return getMyCardsResponseDto;
            } else {
                log.error("아무것도 못가져와요");
            }
        } catch (HttpClientErrorException e) {
            log.info("40X 에러 발생, {}", e.getMessage());
            throw new BusinessException(HttpStatus.NOT_FOUND, "카드사에 해당 상품이 없습니다.");
        }
        log.info("카드사 서버에러 ");
        throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "카드사 서버 에러");
    }

    /**
     * 카드 비활성화
     *
     * @param disableCardRequestDto
     */

    @Override
    public void disableCard(MyCardStatusRequestDto disableCardRequestDto) {

        List<MyCard> myCards = myCardRepository.findAllByMemberId(disableCardRequestDto.getMemberUuid());
        System.out.println("member uuid " + disableCardRequestDto.getMemberUuid());
        System.out.println(myCards);
        boolean cardExists = myCards.stream()
                .anyMatch(myCard -> myCard.getCardNumber().equals(disableCardRequestDto.getCardNumber()));

        if (cardExists) {
            myCardRepository.updateCardStatus(disableCardRequestDto.getCardNumber(), false);
        } else {
            throw new BusinessException(HttpStatus.NOT_FOUND, "요청하신 카드가 없습니다.");
        }
    }

    /**
     * 카드 활성화
     *
     * @param ableCardRequestDto
     */
    @Override
    public void ableCard(MyCardStatusRequestDto ableCardRequestDto) {
        List<MyCard> myCards = myCardRepository.findAllByMemberId(ableCardRequestDto.getMemberUuid());
        boolean cardExists = myCards.stream()
                .anyMatch(myCard -> myCard.getCardNumber().equals(ableCardRequestDto.getCardNumber()));

        if (cardExists) {
            myCardRepository.updateCardStatus(ableCardRequestDto.getCardNumber(), true);
        } else {
            throw new BusinessException(HttpStatus.NOT_FOUND, "요청하신 카드가 없습니다.");
        }
    }

    @Override
    public UUID getMemberId(UUID cardId) {
        MyCard myCard = myCardRepository.findByUuid(cardId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "카드가 존재하지 않습니다."));
        System.out.println("내카드" + myCard.getMemberId());
        return myCard.getMemberId();
    }

    @Override
    public CardHistoryResponseDto getCardHistory(CardHistoryRequestDto cardHistoryRequestDto) {
        log.info("get card history : {}", cardHistoryRequestDto.getCardId());
        // 유효한 날짜인지 객체 변환 시도를 통해 알아낸다
        YearMonth.of(cardHistoryRequestDto.getYear(), cardHistoryRequestDto.getMonth());
        // 유효 확인 후, restClient로 응답 변환
        ResponseEntity<Map> response = restClient.post()
                .uri(paymentUrl + "/analysis/history")
                .contentType(MediaType.APPLICATION_JSON)
                .body(cardHistoryRequestDto)
                .retrieve()
                .toEntity(Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "결과를 가져올 수 없었습니다 : " + response.getBody().get("message"));
        }
        return objectMapper.convertValue(response.getBody().get("data"), CardHistoryResponseDto.class);
    }

    @Override
    public GetMyCardIdsResponseDto getMyCardIds(UUID memberId) {
        List<UUID> cardsIds = myCardQueryRepository.findAllCardIdsByMemberId(memberId);
        if (cardsIds.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "마이 카드에 등록된 카드가 없습니다.");
        }
        return GetMyCardIdsResponseDto.builder()
                .myCardIds(cardsIds)
                .memberId(memberId)
                .build();
    }

    @Override
    @Transactional
    public void initialize() {
        try {
            ResponseEntity<ResultResponse> response = restClient.get()
                    .uri(cardbankUrl + "/card/initialize")
                    .retrieve()
                    .toEntity(ResultResponse.class);
            if(!response.getStatusCode().is2xxSuccessful()) {
                log.error("cardbank initializing has been failed");
                log.error("cancel initializing...");
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "카드 사용량 초기화에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("their is some problem to initialize MyCard of cardbank : {}", e.getMessage());
            log.error("cancel initializing...");
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "카드 사용량 초기화에 실패했습니다.");
        }
        log.info("cardbank initialize done");
        myCardQueryRepository.initialize();
    }

    @Override
    public List<GetMemberCardsDto> getMemberCard(UUID memberId) {
        List<MyCard> myCardList = myCardRepository.findAllByMemberId(memberId);
        List<GetMemberCardsDto> cardsListDto = new ArrayList<>();
        for (MyCard card : myCardList) {
            GetMemberCardsDto dto = GetMemberCardsDto.builder()
                    .memberId(memberId)
                    .cardId(card.getUuid())
                    .amount(card.getAmount())
                    .cardNumber(card.getCardNumber())
                    .build();
            cardsListDto.add(dto);
        }

        return cardsListDto;
    }
}


