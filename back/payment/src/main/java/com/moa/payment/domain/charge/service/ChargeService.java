package com.moa.payment.domain.charge.service;

import com.moa.payment.domain.charge.model.dto.CancelPayRequestDto;
import com.moa.payment.domain.charge.model.dto.GetPaymentLogRequestDto;
import com.moa.payment.domain.charge.model.dto.GetPaymentLogResponseDto;
import com.moa.payment.domain.charge.model.dto.PaymentResultDto;
import com.moa.payment.domain.charge.model.vo.ExecutePaymentRequestVO;
import com.moa.payment.domain.charge.model.vo.ExecutePaymentResultVO;

import java.util.UUID;

public interface ChargeService {
    ExecutePaymentResultVO executePayment(ExecutePaymentRequestVO vo);
    PaymentResultDto makePaymentResultDto(ExecutePaymentResultVO resultVo, ExecutePaymentRequestVO requestVO);
    void sendResultToStore(UUID orderId, ExecutePaymentResultVO vo);
    void CancelPayment(CancelPayRequestDto dto);

    void dutchCancel(UUID paymentId);

    GetPaymentLogResponseDto getPayMentLog(GetPaymentLogRequestDto dto);
}
