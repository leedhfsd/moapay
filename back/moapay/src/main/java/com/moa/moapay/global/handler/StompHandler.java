package com.moa.moapay.global.handler;

import com.moa.moapay.domain.dutchpay.service.DutchPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    public final DutchPayService dutchPayService;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    // TODO: 추후 JWT 검사
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        log.info("command = {}", command);

        if (StompCommand.CONNECT.equals(command)) { // websocket 연결 요청 -> JWT 인증
            log.info("Connect to moapay");
            // JWT 인증
        }

        log.info("header : " + message.getHeaders());
        log.info("message:" + message);

        // 여기서 특정 조건에 따라 메시지를 처리하고 싶다면 추가적인 로직을 구현할 수 있습니다.
        // 예를 들어, 특정 목적지에 대한 메시지를 수신할 경우 다른 처리를 하고 싶을 수 있습니다.
        if ("/pub/dutchpay/join/{roomId}".equals(accessor.getDestination())) {
            log.info("Received a request to join room with ID: {}", accessor.getHeader("roomId"));
            // 필요한 처리를 진행하거나 다른 로직을 추가할 수 있습니다.
        }

        return message; // 메시지를 그대로 반환하여 이후 처리 과정으로 넘김
    }
}
