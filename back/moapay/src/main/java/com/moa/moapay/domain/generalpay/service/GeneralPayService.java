package com.moa.moapay.domain.generalpay.service;

import com.moa.moapay.domain.generalpay.model.dto.ExecuteDutchPayRequestDto;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteGeneralPayRequestDto;
import com.moa.moapay.domain.generalpay.model.dto.ExecuteOfflinePayRequestDto;

import java.util.UUID;

public interface GeneralPayService {
    void executeGeneralPay(ExecuteGeneralPayRequestDto dto);

    void executeDutchPay(ExecuteDutchPayRequestDto dto);

    void executeOfflinePay(ExecuteOfflinePayRequestDto dto);

    void dutchCancel(UUID paymentId);
}
