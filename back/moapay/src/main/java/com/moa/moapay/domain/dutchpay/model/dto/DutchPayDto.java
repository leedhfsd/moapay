package com.moa.moapay.domain.dutchpay.model.dto;

import com.moa.moapay.domain.dutchpay.entity.DutchStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutchPayDto {
    private UUID uuid;
    private UUID memberId;
    private String memberName;
    private Long amount;
    private DutchStatus status;
}
