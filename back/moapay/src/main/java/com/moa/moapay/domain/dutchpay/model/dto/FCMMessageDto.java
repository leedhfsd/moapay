package com.moa.moapay.domain.dutchpay.model.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FCMMessageDto {
    private UUID memberId;
    private String title;
    private String message;
    private UUID roomId;
}
