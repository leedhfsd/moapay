package com.moa.moapay.domain.card.controller;

import com.moa.moapay.domain.card.model.dto.*;
import com.moa.moapay.domain.card.service.MyCardService;
import com.moa.moapay.domain.card.service.RecommendCardService;
import com.moa.moapay.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/card")
public class CardController {

    private final RecommendCardService recommendCardService;
    private final MyCardService myCardService;

    @GetMapping("/recommend/{memberId}")
    public ResponseEntity<ResultResponse> recommend(@PathVariable UUID memberId) {
        RecommendCardResponseDto recommendCardResponseDtos = recommendCardService.recommendCard(memberId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 상품 추천", recommendCardResponseDtos);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/mycard/{memberId}")
    public ResponseEntity<ResultResponse> mycard(@PathVariable UUID memberId) {
        List<GetMyCardsResponseDto> myCardInfo = myCardService.getMyCardInfo(memberId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "나의 카드 조회", myCardInfo);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/cardList")
    public ResponseEntity<ResultResponse> cardList() {
        List<CardInfoResponseDto> allCard = myCardService.getAllCard();
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 전체 조회", allCard);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/getMyCards")
    public ResponseEntity<ResultResponse> getCard(@Valid @RequestBody GetMyCardsRequestDto getMyCardsRequestDto) {
        List<GetMyCardsResponseDto> myCardFromCardBank = myCardService.getMyCardFromCardBank(getMyCardsRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 불러오기", myCardFromCardBank);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<ResultResponse> registrationCard(@Valid @RequestBody CardRegistrationRequestDto registrationRequestDto) {
        log.info("카드추가 정보 : {}", registrationRequestDto);
        GetMyCardsResponseDto getMyCardsResponseDto = myCardService.registrationCard(registrationRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.CREATED, "카드 추가 완료", getMyCardsResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }


    @PostMapping("/disable")
    public ResponseEntity<ResultResponse> disableCard(@Valid @RequestBody MyCardStatusRequestDto disableCardRequestDto ) {
        myCardService.disableCard(disableCardRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 비활성화 완료");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/able")
    public ResponseEntity<ResultResponse> sableCard(@Valid @RequestBody MyCardStatusRequestDto ableCardRequestDto ) {
        myCardService.ableCard(ableCardRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 활성화 완료");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    //payment로 보내는 memberId
    @PostMapping("/getMemberId")
    public ResponseEntity<UUID> getMemberId(@Valid @RequestBody UUID cardId){
        UUID memberId= myCardService.getMemberId(cardId);
        return ResponseEntity.ok(memberId);
    }

    @PostMapping("/history")
    public ResponseEntity<ResultResponse> getCardHistory(@RequestBody CardHistoryRequestDto cardHistoryRequestDto){
        CardHistoryResponseDto resultDto = myCardService.getCardHistory(cardHistoryRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "달 별 결제내역을 가져왔습니다.", resultDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/getMyCardIds")
    public ResponseEntity<ResultResponse> getCardIds(@RequestBody GetMyCardIdsRequestDto getMyCardIdsRequestDto){
        GetMyCardIdsResponseDto resultDto = myCardService.getMyCardIds(getMyCardIdsRequestDto.getMemberId());
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "등록 마이카드 UUID들을 불러왔습니다.", resultDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
    //paymnet로 보낼 myCard
    @PostMapping("/getMemberCard")
    public ResponseEntity<List<GetMemberCardsDto>> getMemberCard(@Valid @RequestBody UUID memberId){
        List<GetMemberCardsDto> cards= myCardService.getMemberCard(memberId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/initialize")
    public ResponseEntity<ResultResponse> initialize() {
        log.info("initialize card amount and benefit usage");
        myCardService.initialize();
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 사용량을 초기화하였습니다.");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
