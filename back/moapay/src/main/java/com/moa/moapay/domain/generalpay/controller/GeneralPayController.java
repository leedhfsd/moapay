package com.moa.moapay.domain.generalpay.controller;

import com.moa.moapay.domain.card.entity.MyCard;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteGeneralPayRequestDto;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteOfflinePayRequestDto;
import com.moa.moapay.domain.generalpay.service.GeneralPayService;
import com.moa.moapay.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/generalpay")
public class GeneralPayController {

    private final GeneralPayService generalPayService;

    @PostMapping("/pay")
    public ResponseEntity<ResultResponse> executeGeneralPay(@RequestBody ExecuteGeneralPayRequestDto dto) {
        // 응답은 비동기식 SSE로 보낼 예정이므로, response 본문은 비워서 보낸다
        log.info("execute online pay : {}", dto.toString());
        generalPayService.executeGeneralPay(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "온라인 결제 요청 전송 완료");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/offline")
    public ResponseEntity<ResultResponse> executeOfflinePay(@RequestBody ExecuteOfflinePayRequestDto dto) {
        log.info("execute offline pay : {}", dto.getBarcode());
        generalPayService.executeOfflinePay(dto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "오프라인 결제 요청 전송 완료");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

}
