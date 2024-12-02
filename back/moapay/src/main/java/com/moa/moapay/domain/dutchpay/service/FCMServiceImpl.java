package com.moa.moapay.domain.dutchpay.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.moa.moapay.domain.dutchpay.model.dto.FCMMessageDto;
import com.moa.moapay.domain.dutchpay.model.dto.FCMTokenDto;
import com.moa.moapay.domain.dutchpay.repository.FCMRedisRepository;
import com.moa.moapay.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {

    private final FCMRedisRepository fcmRedisRepository;

    @Override
    public void saveToken(FCMTokenDto fcmTokenDto) {

        log.info(fcmTokenDto.toString());
        // 회원 유효성 검사 해야 할듯
        String token = fcmRedisRepository.getToken(String.valueOf(fcmTokenDto.getMemberId()));

        if (token != null) {
            // 기존에 저장된 토큰이 동일한지 확인
            if (token.equals(fcmTokenDto.getToken())) {
                // 기존 토큰과 동일하면 에러 처리
                throw new BusinessException(HttpStatus.CONFLICT, "이미 동일한 FCM 토큰이 존재합니다.");
            }
            // 기존 토큰이 존재하지만 다른 경우: 삭제 후 새 토큰 저장
            fcmRedisRepository.deleteToken(String.valueOf(fcmTokenDto.getMemberId()));
        }

        // 새 토큰 저장
        fcmRedisRepository.saveToken(String.valueOf(fcmTokenDto.getMemberId()), fcmTokenDto.getToken(), 10000L);
    }


    @Override
    public void pushNotification(FCMMessageDto fcmMessageDto) {

        log.info("FCM 전송");

        String token = fcmRedisRepository.getToken(String.valueOf(fcmMessageDto.getMemberId()));
        if (token == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "토큰이 없습니다");
        }
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setWebpushConfig(WebpushConfig.builder()
//                            .putHeader("ttl", "300")
                            .setNotification(new WebpushNotification("MoaPay", fcmMessageDto.getMessage()))
                            .putData("roomId", String.valueOf(fcmMessageDto.getRoomId()))
                            .build())
                    .build();
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info(response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
