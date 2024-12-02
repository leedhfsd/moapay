package com.moa.moapay.global.kafkaVo;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMsgVo {

    private String paymentType;
    private String categoryId;

    private UUID merchantId;
    private UUID cardId;
    private String cardNumber;
    private String cvc;
    private long amount;

    @Override
    public String toString() {
        return "KafkaMsgVo{" +
                "merchantId=" + merchantId +
                ", cardId=" + cardId +
                ", cardNumber='" + cardNumber + '\'' +
                ", cvc='" + cvc + '\'' +
                ", amount=" + amount +
                '}';
    }
}
