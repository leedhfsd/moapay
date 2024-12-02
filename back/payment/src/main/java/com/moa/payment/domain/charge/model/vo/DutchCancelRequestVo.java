package com.moa.payment.domain.charge.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class DutchCancelRequestVo {
    private String paymentId;
}
