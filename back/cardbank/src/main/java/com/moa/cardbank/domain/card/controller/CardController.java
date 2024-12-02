package com.moa.cardbank.domain.card.controller;

import com.moa.cardbank.domain.card.model.dto.*;
import com.moa.cardbank.domain.card.model.dto.getMyCard.GetMyCardsRequestDto;
import com.moa.cardbank.domain.card.model.dto.getMyCard.GetMyCardsResponseDto;
import com.moa.cardbank.domain.card.service.CardService;
import com.moa.cardbank.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/getMyCards")
    public ResponseEntity<?> getMyCards(@Valid @RequestBody GetMyCardsRequestDto getMyCardsRequestDto) {
        List<GetMyCardsResponseDto> responseDto = cardService.getMyCards(getMyCardsRequestDto);
        log.info("return = {}", responseDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "내 카드 정보 조회", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    /* 일반 카드 API */
    @PostMapping("/create")
    public ResponseEntity<ResultResponse> createCardProduct(@RequestBody CreateCardProductRequestDto dto){
        log.info("create card product : {}", dto.getName());
        CreateCardProductResponseDto responseDto = cardService.createCardProduct(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 상품을 추가했습니다.", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    /* 결제 관련 API */
    @PostMapping("/pay")
    public ResponseEntity<ResultResponse> executePay(@RequestBody ExecutePayRequestDto dto) {
        log.info("payment request : {}", dto.getCardNumber());
        ExecutePayResponseDto responseDto = cardService.executePay(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "결제 처리 완료", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ResultResponse> cancelPay(@RequestBody CancelPayRequestDto dto) {
        log.info("cancel payment : {}", dto.getPaymentId());
        CancelPayResponseDto responseDto  = cardService.cancelPay(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "결제 취소 완료", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    /* 개인 카드 관련 API */
    @PostMapping("/my/create")
    public ResponseEntity<ResultResponse> createMycard(@RequestBody CreateMyCardRequestDto dto) {
        log.info("create my card");
        CreateMyCardResponseDto responseDto = cardService.createMyCard(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "개인 카드 생성됨", responseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody CardRegistrationRequestDto registrationRequestDto) {
        log.info("registration request : {}", registrationRequestDto.getCardNumber());
        CardRestResponseDto cardRestResponseDto = cardService.registration(registrationRequestDto);
        log.info(cardRestResponseDto.toString());
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 추가 확인", cardRestResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/initialize")
    public ResponseEntity<ResultResponse> initialize() {
        log.info("initialize card amount and benefit usage");
        cardService.initialize();
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "카드 사용량을 초기화하였습니다.");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }


}
