package com.moa.moapay.domain.dutchpay.controller;

import com.moa.moapay.domain.dutchpay.model.dto.*;
import com.moa.moapay.domain.dutchpay.service.DutchPayService;
import com.moa.moapay.domain.dutchpay.service.FCMServiceImpl;
import com.moa.moapay.global.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/dutchpay")
public class DutchPayController {

    private final DutchPayService dutchPayService;
    private final FCMServiceImpl fcmService;
    // 방 생성 엔드포인트
    @PostMapping("/createRoom")
    public ResponseEntity<ResultResponse> createRoom(@Valid @RequestBody DutchPayStartRequestDto dutchPayStartRequestDto) {
        log.info("Creating Dutch pay room");
        UUID uuid = dutchPayService.createDutchRoom(dutchPayStartRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.CREATED, "더치페이 룸 생성", uuid);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/getDutchRoomInfo/{roomId}")
    public ResponseEntity<ResultResponse> getDutchRoomInfo(@PathVariable UUID roomId) {
        log.info("Getting Dutch pay room info");
        DutchRoomInfo dutchRoomInfo = dutchPayService.getDutchRoomInfo(roomId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "더치페이 룸 정보 조회", dutchRoomInfo);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/orderInfo/{orderId}")
    public ResponseEntity<ResultResponse> getOrderInfo(@PathVariable UUID orderId) {
        log.info("Getting Dutch pay order info");
        SimpleOrderInfoDto orderInfo = dutchPayService.getSimpleOrderInfoFromRedis(orderId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "더치페이 결제 정보 조회", orderInfo);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment(@Valid @RequestBody DutchPayPaymentRequsetDto dutchPayPaymentRequsetDto) {
        log.info("Payment request: {}", dutchPayPaymentRequsetDto);
        dutchPayService.dutchpayPayment(dutchPayPaymentRequsetDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "더치페이 결제 요청이 성공 했습니다.");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/fcmTokens")
    public ResponseEntity<?> fcmToken(@Valid @RequestBody FCMTokenDto fcmTokenDto) {
        log.info("FCM token: {}", fcmTokenDto.getToken());
        fcmService.saveToken(fcmTokenDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "FCM 토큰 저장 완료");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/fcmSend")
    public ResponseEntity<?> fcmSend(@Valid @RequestBody FCMMessageDto fcmMessageDto) {
        log.info("FCM token: {}", fcmMessageDto.toString());
        fcmService.pushNotification(fcmMessageDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "FCM 전송");
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/getDutchByMember/{memberId}")
    public ResponseEntity<ResultResponse> getDutchByMember(@PathVariable UUID memberId) {
        log.info("getDutchByMember: {}", memberId);
        DutchRoomInfo dutchRoomByMember = dutchPayService.getDutchRoomByMember(memberId);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "맴버아이디로 더치페이 방 정보 조회", dutchRoomByMember);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/getMyPrice")
    public ResponseEntity<?> getMyPrice(@Valid @RequestBody DutchGetMyPriceRequestDto dutchGetMyPriceRequestDto) {
        log.info("getMyPrice : {}", dutchGetMyPriceRequestDto.toString());
        GetMyPriceResponseDto myPrice = dutchPayService.getMyPrice(dutchGetMyPriceRequestDto);
        ResultResponse resultResponse = ResultResponse.of(HttpStatus.OK, "더치페이 가격 조회", myPrice);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }


    @PostMapping("/cancel")
    public ResponseEntity<?> cancelDutchPay(@RequestParam DutchPayRoomLeaveDto roomLeaveDto) {

        dutchPayService.cancelDutchRoom(roomLeaveDto);

        return null;
    }

    /**
     * 더치페이 완료 전송용 sse
     * @param id
     * @return
     */
    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable UUID id) {
        // 클라이언트에서 결제 요청을 보내기 전, 구독을 해놓는다
        log.info("Subscribing to notification with id {}", id);

        return null;
    }
}
