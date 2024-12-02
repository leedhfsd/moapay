package com.moa.moapay.domain.dutchpay.service;

import com.moa.moapay.domain.dutchpay.model.dto.FCMMessageDto;
import com.moa.moapay.domain.dutchpay.model.dto.FCMTokenDto;

public interface FCMService {
    void saveToken(FCMTokenDto fcmTokenDto);
    void pushNotification(FCMMessageDto fcmMessageDto);
}
