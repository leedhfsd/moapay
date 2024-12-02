package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DutchPayRoomJoinDto {
    private UUID memberId;
    private String memberName;
}
