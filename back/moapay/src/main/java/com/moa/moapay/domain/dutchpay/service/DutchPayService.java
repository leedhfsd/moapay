package com.moa.moapay.domain.dutchpay.service;

import com.moa.moapay.domain.dutchpay.model.dto.*;
import com.moa.moapay.domain.dutchpay.model.vo.DutchPayCompleteVo;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface DutchPayService {

    // 더치 룸 생성
    @Transactional
    UUID createDutchRoom(DutchPayStartRequestDto dutchPayStartRequestDto);

    @Transactional
    void extracted(UUID RoomId, List<ConfirmPriceDto> dutchPayDtoList);

    // 더치 룸 정보 가져오기
    DutchRoomInfo getDutchRoomInfo(UUID roomId);

    // 더치 룸 입장
    @Transactional
    List<DutchPayDto> joinDutchRoom(UUID roomId, DutchPayRoomJoinDto dutchPayRoomJoinDto);

    // 더치룸 떠나기
    @Transactional
    List<DutchPayDto> leaveDutchRoom(DutchPayRoomLeaveDto dutchPayRoomLeaveDto);

    @Transactional
    DutchRoomInfo confirm(DutchPayConfirmRequestDto dutchPayModifyRequestDto);

    void dutchpayPayment(@Valid DutchPayPaymentRequsetDto dutchPayPaymentRequsetDto);

    void dutchpayComplite(DutchPayCompleteVo dutchPayCompleteVo);

    DutchRoomInfo getDutchRoomByMember(UUID memberId);

    void cancelDutchRoom(DutchPayRoomLeaveDto roomLeaveDto);

    SimpleOrderInfoDto getSimpleOrderInfoFromStore(UUID orderId);
    SimpleOrderInfoDto getSimpleOrderInfoFromRedis(UUID orderId);

    GetMyPriceResponseDto getMyPrice( DutchGetMyPriceRequestDto dutchGetMyPriceRequestDto);
}
