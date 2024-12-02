package com.moa.moapay.domain.dutchpay.model.dto;

import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DutchPayPaymentRequsetDto {
    // 더치페이 정보
    private UUID dutchPayId;
    private UUID dutchRoomId;
    private UUID memberId;

    private UUID requestId; // 프론트에서 생성해서 전달, 전송 버튼 누를 때마다 생성하지 말고 할 것
    private UUID orderId;
    private UUID merchantId;
    //    private String categoryId;
    private CardSelectionType cardSelectionType;
    private long totalPrice;
    private String cardNumber;
    private String cvc;
}
