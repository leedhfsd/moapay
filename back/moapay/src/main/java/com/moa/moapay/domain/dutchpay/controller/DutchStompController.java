package com.moa.moapay.domain.dutchpay.controller;

import com.moa.moapay.domain.dutchpay.model.dto.*;
import com.moa.moapay.domain.dutchpay.service.DutchPayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Controller
@MessageMapping("/dutchpay")
@RequiredArgsConstructor
@Slf4j
public class DutchStompController {

    private final DutchPayService dutchPayService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방 참여 컨트롤러
     * @param roomId
     * @param dutchPayRoomJoinDto
     */
    @MessageMapping("/join/{roomId}")
    @SendTo("/sub/dutch-room/{roomId}")
    public List<DutchPayDto> joinRoom(@DestinationVariable UUID roomId, @Valid @RequestBody DutchPayRoomJoinDto dutchPayRoomJoinDto) {
        log.info("Joining Dutch room with roomId = {}, {}", roomId, dutchPayRoomJoinDto.getMemberId());
        List<DutchPayDto> dutchPayDto = dutchPayService.joinDutchRoom(roomId, dutchPayRoomJoinDto);
        return dutchPayDto;
    }

    /**
     * 방 떠나기 컨트롤러
     * @param dutchPayRoomLeaveDto
     */
    @MessageMapping("/leave/{roomId}")
    @SendTo("/sub/dutch-room/{roomId}")
    public List<DutchPayDto> leaveRoom(@Valid @RequestBody DutchPayRoomLeaveDto dutchPayRoomLeaveDto) {
        List<DutchPayDto> dutchPayDtoList = dutchPayService.leaveDutchRoom(dutchPayRoomLeaveDto);
        return dutchPayDtoList;
    }

    /**
     * 더치페이 결정 컨트롤러
     * @param dutchPayModifyRequestDto
     */
    @MessageMapping("/confirm/{roomId}")
    @SendTo("/sub/dutch-room/{roomId}")
    public DutchRoomInfo confirm(@Valid @RequestBody DutchPayConfirmRequestDto dutchPayModifyRequestDto) {
        log.info("confirm");
        DutchRoomInfo confirm = dutchPayService.confirm(dutchPayModifyRequestDto);
        return confirm;
    }

    /**
     * 더치페이 방 정보 가져오기 컨트롤러
     * @param roomId
     */
    @MessageMapping("/dutchRoom/{roomId}")
    @SendTo("/sub/dutch-room/{roomId}")
    public void getDutchRoomInfo(@DestinationVariable UUID roomId) {
        log.info("Getting Dutch room info");
        dutchPayService.getDutchRoomInfo(roomId);
    }
}
