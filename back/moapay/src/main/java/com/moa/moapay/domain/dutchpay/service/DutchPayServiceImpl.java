package com.moa.moapay.domain.dutchpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.moapay.domain.dutchpay.entity.DutchPay;
import com.moa.moapay.domain.dutchpay.entity.DutchRoom;
import com.moa.moapay.domain.dutchpay.entity.DutchStatus;
import com.moa.moapay.domain.dutchpay.model.dto.*;
import com.moa.moapay.domain.dutchpay.model.vo.DutchPayCompleteVo;
import com.moa.moapay.domain.dutchpay.repository.DutchPayRedisRepository;
import com.moa.moapay.domain.dutchpay.repository.DutchPayRepository;
import com.moa.moapay.domain.dutchpay.repository.DutchRoomRepository;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteDutchPayRequestDto;
import com.moa.moapay.domain.generalpay.model.vo.PaymentCardInfoVO;
import com.moa.moapay.domain.generalpay.service.GeneralPayService;
import com.moa.moapay.global.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DutchPayServiceImpl implements DutchPayService {

    private final DutchPayRepository dutchPayRepository;
    private final DutchRoomRepository dutchRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final HttpMessageConverters messageConverters;
    private final GeneralPayService generalPayService;
    private final FCMService fcmService;
    private final DutchPayRedisRepository dutchPayRedisRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${external-url.payment}")
    private String paymentUrl;

    @Value("${external-url.store}")
    private String storeUrl;

    @Override
    @Transactional
    public UUID createDutchRoom(DutchPayStartRequestDto dutchPayStartRequestDto) {
        log.info(dutchPayStartRequestDto.toString());
        DutchRoom dutchRoom = DutchRoom.builder()
                .totalPrice(dutchPayStartRequestDto.getTotalPrice())
                .merchantName(dutchPayStartRequestDto.getMerchantName())
                .merchantId(dutchPayStartRequestDto.getMerchantId())
                .categoryId(dutchPayStartRequestDto.getCategoryId())
                .orderId(dutchPayStartRequestDto.getOrderId())
                .maxPerson(dutchPayStartRequestDto.getMaxMember())
                .managerId(dutchPayStartRequestDto.getMemberId())
                .status(DutchStatus.READY)
                .build();
        dutchRoomRepository.save(dutchRoom);
        // 이후 상품정보도 redis에 등록
        SimpleOrderInfoDto orderInfo = getSimpleOrderInfoFromStore(dutchPayStartRequestDto.getOrderId());
        dutchPayRedisRepository.RegisterSimpleOrderInfo(dutchPayStartRequestDto.getOrderId(), orderInfo);
        return dutchRoom.getUuid();
    }

    // 더치페이 방에 참여
    @Override
    @Transactional
    public List<DutchPayDto> joinDutchRoom(UUID roomId, DutchPayRoomJoinDto dutchPayRoomJoinDto) {
        // 방이 있는지 확인
        DutchRoom existingRoom = validateRoomExists(roomId);

        long max = existingRoom.getMaxPerson();
        log.info("max = {}", max);

        // 방장 여부 확인
        boolean isManager = existingRoom.getManagerId().equals(dutchPayRoomJoinDto.getMemberId());

        List<DutchPay> dutchPays = dutchPayRepository.findByRoomUuid(roomId);
        
        // 중복 등록 방지
        for (DutchPay dutchPay : dutchPays) {
            if(dutchPay.getMemberId().equals(dutchPayRoomJoinDto.getMemberId())) {
                throw new BusinessException(HttpStatus.NOT_EXTENDED, "이미 등록된 회원");  
            }
        }

        // 수용 인원 초과
        if(existingRoom.getDutchPayList().size() >= max) {
            throw new BusinessException(HttpStatus.NOT_EXTENDED, "수용인원 초과");
        }
        // 이미 진행중인 방
        if(!existingRoom.getStatus().equals(DutchStatus.READY)) {
            throw new BusinessException(HttpStatus.NOT_EXTENDED, "더치페이 방에 입장할 수 없습니다");
        }

        DutchPay dutchPay = DutchPay.builder().memberId(dutchPayRoomJoinDto.getMemberId())
                .memberName(dutchPayRoomJoinDto.getMemberName())
                .payStatus(DutchStatus.JOIN)
                .roomEntity(existingRoom)
                .uuid(UUID.randomUUID())
                .isManager(isManager)
                .build();

        // 새로운 더치페이 정보 저장
        dutchPayRepository.save(dutchPay);
//        existingRoom = validateRoomExists(roomId);

        List<DutchPay> dutchPayList = dutchPayRepository.findByRoomUuid(roomId);

        List<DutchPayDto> dutchPayDtoList = dutchPayList.stream().map(
                dutch -> {
                    return DutchPayDto.builder()
                            .memberId(dutch.getMemberId())
                            .status(dutch.getPayStatus())
                            .amount(dutch.getAmount())
                            .uuid(dutch.getUuid())
                            .memberName(dutch.getMemberName())
                            .build();
                }
        ).collect(Collectors.toList());

//        DutchPayDto dutchPayDto = DutchPayDto
//                .builder()
//                .amount(dutchPay.getAmount())
//                .uuid(dutchPay.getUuid())
//                .status(dutchPay.getStatus())
//                .memberId(dutchPay.getMemberId())
//                .memberName(dutchPay.getMemberName())
//                .build();
        // 사용자 입장 정보 보내주기
//        getDutchRoomInfo(roomId);

        return dutchPayDtoList;
    }


    @Override
    @Transactional
    public List<DutchPayDto> leaveDutchRoom(DutchPayRoomLeaveDto dutchPayRoomLeaveDto) {
        UUID roomId = dutchPayRoomLeaveDto.getRoomId();
        DutchRoom existingRoom = validateRoomExists(roomId);  // 방 존재 여부 검증

        try {
            dutchPayRepository.deleteDutchPayByUuid(dutchPayRoomLeaveDto.getMemberId(), existingRoom.getUuid());
        } catch (EntityNotFoundException e) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "해당 멤버가 존재하지 않습니다.");
        }

        // TODO : UUID 가 겹치면 고장남
        dutchRoomRepository.decrementParticipantCount(roomId); // 참가자 수 감소
        if (existingRoom.getCurPerson() == 1) {
            dutchRoomRepository.updateDutchRoomStatus(DutchStatus.CANCEL);
        }

        List<DutchPay> dutchPayList = dutchPayRepository.findByRoomUuid(roomId);

        List<DutchPayDto> dutchPayDtoList = dutchPayList.stream().map(
                dutch -> {
                    return DutchPayDto.builder()
                            .memberId(dutch.getMemberId())
                            .status(dutch.getPayStatus())
                            .amount(dutch.getAmount())
                            .uuid(dutch.getUuid())
                            .memberName(dutch.getMemberName())
                            .build();
                }
        ).collect(Collectors.toList());

        // 사용자 퇴장 정보 보여주기
        return dutchPayDtoList;
    }

    // 방 존재 여부를 검증하는 메서드
    public DutchRoom validateRoomExists(UUID roomId) {
        DutchRoom dutchRoom = dutchRoomRepository.findByUuid(roomId);
        log.info(dutchRoom.getUuid().toString());

        if (dutchRoom == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "요청하신 더치페이 방이 없습니다");
        }
        return dutchRoom;
    }

    // 방의 값 확인
    @Override
    @Transactional
    public DutchRoomInfo confirm(DutchPayConfirmRequestDto dutchPayConfirmRequestDto) {

        log.info(dutchPayConfirmRequestDto.getConfirmPriceDtos().get(0).getMemberId().toString());
        List<ConfirmPriceDto> dutchPayDtoList = dutchPayConfirmRequestDto.getConfirmPriceDtos();
        // 방의 참가자 수 설정
        dutchRoomRepository.updateDutchRoomConfirm(dutchPayConfirmRequestDto.getMemberCnt(), DutchStatus.PROGRESS, dutchPayConfirmRequestDto.getRoomId());
        extracted(dutchPayConfirmRequestDto.getRoomId(), dutchPayDtoList);

        DutchRoom dutchRoom = dutchRoomRepository.findByRoomId(dutchPayConfirmRequestDto.getRoomId());

        List<DutchPayDto> dutchPayDtos = dutchRoom.getDutchPayList().stream().map(
                dutch -> {
                    return DutchPayDto.builder()
                            .memberId(dutch.getMemberId())
                            .status(dutch.getPayStatus())
                            .amount(dutch.getAmount())
                            .uuid(dutch.getUuid())
                            .memberName(dutch.getMemberName())
                            .build();
                }
        ).collect(Collectors.toList());

        DutchRoomInfo dutchRoomInfo = DutchRoomInfo.builder()
                .statusRoom(dutchRoom.getStatus())
                .dutchUuid(dutchRoom.getUuid())
                .categoryId(dutchRoom.getCategoryId())
                .memberCnt(dutchRoom.getCurPerson())
                .merchantName(dutchRoom.getMerchantName())
                .merchantId(dutchRoom.getMerchantId())
                .orderId(dutchRoom.getOrderId())
                .totalPrice(dutchRoom.getTotalPrice())
                .dutchPayList(dutchPayDtos)
                .build();

        return dutchRoomInfo;
    }

    @Override
    @Transactional
    public void dutchpayPayment(DutchPayPaymentRequsetDto dutchPayPaymentRequsetDto) {

        DutchPay dutchPay = dutchPayRepository.findByUuid(dutchPayPaymentRequsetDto.getDutchPayId());

        log.info("dutchPayID : {}", dutchPayPaymentRequsetDto.getDutchPayId());

        if(dutchPay.getPayStatus().equals(DutchStatus.READY)) {
            ExecuteDutchPayRequestDto executeGeneralPayRequestDto = ExecuteDutchPayRequestDto.builder()
                    .dutchPayId(dutchPayPaymentRequsetDto.getDutchPayId())
                    .requestId(dutchPayPaymentRequsetDto.getRequestId())
                    .cvc(dutchPayPaymentRequsetDto.getCvc())
                    .cardNumber(dutchPayPaymentRequsetDto.getCardNumber())
                    .cardSelectionType(dutchPayPaymentRequsetDto.getCardSelectionType())
                    .requestId(dutchPayPaymentRequsetDto.getRequestId())
                    .totalPrice(dutchPayPaymentRequsetDto.getTotalPrice())
                    .merchantId(dutchPayPaymentRequsetDto.getMerchantId())
                    .orderId(dutchPayPaymentRequsetDto.getOrderId())
                    .build();

            generalPayService.executeDutchPay(executeGeneralPayRequestDto);
            dutchPayRepository.updateStatus(dutchPayPaymentRequsetDto.getMemberId(),dutchPayPaymentRequsetDto.getDutchRoomId(),DutchStatus.PROGRESS);
        } else {
            throw new BusinessException(HttpStatus.ALREADY_REPORTED, "이미 결제요청이 진행중입니다.");
        }

    }


    @Override
    @Transactional
    public void dutchpayComplite(DutchPayCompleteVo dutchPayCompleteVo) {

        UUID dutchUuid = dutchPayCompleteVo.getDutchUuid();
        String status = dutchPayCompleteVo.getStatus();

        DutchPay byUuid = dutchPayRepository.findByUuid(dutchUuid);
        DutchRoom byDutchUuid = dutchRoomRepository.findByDutchUuid(dutchUuid);

        if(status.equals("CANCEL")){
            // 지금은 안쓰는 로직
            List<DutchPay> dutchPayList = byDutchUuid.getDutchPayList();

            for(DutchPay dutchPay : dutchPayList){
                if(dutchPay.getPayStatus().equals(DutchStatus.DONE)){

                    String requestId = dutchPayRedisRepository.getToken(dutchUuid.toString());
                    UUID.fromString(requestId);

                    List<PaymentCardInfoVO> paymentInfoList = dutchPayCompleteVo.getPaymentInfoList();
                    // 취소 요청
                    DutchCancelDto dutchCancelDto = DutchCancelDto.builder()
                            .paymentId(dutchPayCompleteVo.getRequestId())
                            .cardId(paymentInfoList.get(0).getCardId())
                            .cardNumber(paymentInfoList.get(0).getCardNumber())
                            .cvc(paymentInfoList.get(0).getCvc())
                            .build();
                    ResponseEntity<Map> paymentResponse = restClient.post()
                            .uri(paymentUrl+"/charge/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(dutchCancelDto)
                            .retrieve()
                            .toEntity(Map.class);
                }
                //TODO : 이거 바꿔야해
                dutchPayRepository.updateByDutchUuid(dutchPay.getUuid(), DutchStatus.CANCEL);
                FCMMessageDto fcm = FCMMessageDto.builder()
                        .memberId(dutchPay.getMemberId())
                        .title("MoaPay")
                        .roomId(byDutchUuid.getUuid())
                        .message("더치페이가 중단 되었습니다.")
                        .build();
                fcmService.pushNotification(fcm);
            }
        }
        else if(status.equals("PROGRESS")){
            // 결제 요청 다시
            FCMMessageDto fcm = FCMMessageDto.builder()
                    .memberId(byUuid.getMemberId())
                    .title("MoaPay")
                    .roomId(byDutchUuid.getUuid())
                    .message("결제 실패")
                    .build();
            fcmService.pushNotification(fcm);

            log.info("결제 실패");
            dutchPayRepository.updateByDutchUuid(dutchUuid, DutchStatus.READY);
        }
        else if(status.equals("DONE")){
            // 결제 상태 변경
            dutchPayRepository.updateByDutchUuid(dutchUuid, DutchStatus.DONE);

//            List<DutchPay> dutchPayList = byDutchUuid.getDutchPayList();
            List<DutchPay> dutchPayList = dutchPayRepository.findByRoomUuid(byDutchUuid.getUuid());
            DutchPay dutchPayMember = dutchPayRepository.findByUuid(dutchUuid);

            dutchPayRedisRepository.save(dutchUuid.toString(), dutchPayCompleteVo.getRequestId().toString());

            boolean flag = true;
            log.info("DutchPay size() : {}", dutchPayList.size());

            for (DutchPay dutchPay : dutchPayList) { // 해당 더치 내역 다돌면서
                DutchStatus dutchStatus = dutchPay.getPayStatus();
                if (dutchPay.getUuid().equals(dutchPayCompleteVo.getDutchUuid())) {
                    log.info("{} 님 더치 완료", dutchPay.getMemberName());
                    continue;
                }
                if(!dutchStatus.equals(DutchStatus.DONE)) {
                    log.info("더치 완료 ?? {}, {}", dutchPay.getMemberName(), dutchPay.getPayStatus());
                    flag = false;
                }

                log.info("더치페이 : {}", dutchPay.getPayStatus());

                FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                        .memberId(dutchPay.getMemberId())
                        .title("MoaPay")
                        .roomId(byDutchUuid.getUuid())
                        .message(dutchPayMember.getMemberName() + " 님의 더치페이 결제가 완료되었습니다.")
                        .build();
                fcmService.pushNotification(fcmMessageDto);
            }

            if(flag) {
                log.info("더치페이 완료");
                for(DutchPay dutchPay : dutchPayList) {
                    FCMMessageDto fcmMessageDto = FCMMessageDto.builder()
                            .memberId(dutchPay.getMemberId())
                            .title("MoaPay")
                            .roomId(byDutchUuid.getUuid())
                            .message("더치페이가 완료되었습니다.")
                            .build();
                    fcmService.pushNotification(fcmMessageDto);
                }
                dutchRoomRepository.updateDutchRoomConfirm(byDutchUuid.getCurPerson(), DutchStatus.DONE, byDutchUuid.getUuid());
            }

        }


    }

    @Override
    public DutchRoomInfo getDutchRoomByMember(UUID memberId) {

        DutchRoom dutchRoom = dutchRoomRepository.findByDutchUuid(memberId);

        List<DutchPay> dutchPayList = dutchRoom.getDutchPayList();

        List<DutchPayDto> dutchPayDtoList = dutchPayList.stream().map(
                dutchPay -> {
                    return DutchPayDto.builder()
                            .memberId(dutchPay.getMemberId())
                            .status(dutchPay.getPayStatus())
                            .amount(dutchPay.getAmount())
                            .uuid(dutchPay.getUuid())
                            .memberName(dutchPay.getMemberName())
                            .build();
                }
        ).toList();

        DutchRoomInfo dutchRoomInfoByMemberId = DutchRoomInfo.builder()
                .statusRoom(dutchRoom.getStatus())
                .dutchUuid(dutchRoom.getUuid())
                .categoryId(dutchRoom.getCategoryId())
                .memberCnt(dutchRoom.getCurPerson())
                .merchantName(dutchRoom.getMerchantName())
                .merchantId(dutchRoom.getMerchantId())
                .orderId(dutchRoom.getOrderId())
                .totalPrice(dutchRoom.getTotalPrice())
                .dutchPayList(dutchPayDtoList)
                .build();

        return dutchRoomInfoByMemberId;
    }

    @Override
    @Transactional
    public void cancelDutchRoom(DutchPayRoomLeaveDto roomLeaveDto) {
        UUID dutchUuid = roomLeaveDto.getRoomId();
        DutchRoom byDutchUuid = dutchRoomRepository.findByUuid(dutchUuid);


    }

    @Override
    public SimpleOrderInfoDto getSimpleOrderInfoFromStore(UUID orderId) {
        try {
            ResponseEntity<Map> res = restClient.get()
                    .uri(storeUrl + "/order/simple/"+orderId.toString())
                    .retrieve()
                    .toEntity(Map.class);
            return objectMapper.convertValue(res.getBody().get("data"), SimpleOrderInfoDto.class);
        } catch (Exception e) {
            log.error("failed to register order info");
            return null;
        }
    }

    @Override
    public SimpleOrderInfoDto getSimpleOrderInfoFromRedis(UUID orderId) {
        return dutchPayRedisRepository.GetSimpleOrderInfo(orderId);
    }

    @Override
    public GetMyPriceResponseDto getMyPrice(DutchGetMyPriceRequestDto dutchGetMyPriceRequestDto) {

        DutchPay dutchPay = dutchPayRepository.findByRoomIdAndMemberId(dutchGetMyPriceRequestDto.getRoomId(), dutchGetMyPriceRequestDto.getMemberId());

        log.info(dutchPay.getAmount().toString());
        long price = dutchPay.getAmount();
        log.info("price = {}", price);

        GetMyPriceResponseDto getMyPriceResponseDto = GetMyPriceResponseDto.builder().price(price).build();
        return getMyPriceResponseDto;
    }

    @Override
    @Transactional
    public void extracted(UUID roomId, List<ConfirmPriceDto> dutchPayDtoList) {
        // 더치 페이 정보 수정
        for(ConfirmPriceDto confirmPriceDto : dutchPayDtoList) {
            dutchPayRepository.updateAmountByMemberId(confirmPriceDto.getPrice(), confirmPriceDto.getMemberId(), roomId, DutchStatus.READY);
        }
    }

    @Override
    @Transactional
    public DutchRoomInfo getDutchRoomInfo(UUID roomId) {
        log.info("roomID : {}", roomId.toString());
        DutchRoom dutchRoom = dutchRoomRepository.findByRoomId(roomId);

        List<DutchPay> dutchPayList = dutchRoom.getDutchPayList();
        List<DutchPayDto> dutchPayDtoList = dutchPayList.stream().map(
                dutchPay -> {
                    return DutchPayDto.builder()
                            .memberName(dutchPay.getMemberName())
                            .memberId(dutchPay.getMemberId())
                            .amount(dutchPay.getAmount())
                            .status(dutchPay.getPayStatus())
                            .uuid(dutchPay.getUuid())
                            .build();
                }
        ).collect(Collectors.toList());

        DutchRoomInfo roomInfo = DutchRoomInfo.builder()
                .statusRoom(dutchRoom.getStatus())
                .merchantName(dutchRoom.getMerchantName())
                .memberCnt(dutchRoom.getCurPerson())
                .merchantId(dutchRoom.getMerchantId())
                .dutchUuid(dutchRoom.getUuid())
                .orderId(dutchRoom.getOrderId())
                .totalPrice(dutchRoom.getTotalPrice())
                .categoryId(dutchRoom.getCategoryId())
                .dutchPayList(dutchPayDtoList)
                .build();

        messagingTemplate.convertAndSend("/sub/dutch-room/" + roomId, roomInfo);

        return roomInfo;
    }
}
