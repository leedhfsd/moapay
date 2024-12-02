package com.moa.moapay.domain.generalpay.model.dto;

import com.moa.moapay.domain.card.model.RecommendType;
import com.moa.moapay.domain.generalpay.model.CardSelectionType;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExecuteDutchPayRequestDto {
    private UUID requestId; // 프론트에서 생성해서 전달, 전송 버튼 누를 때마다 생성하지 말고 처음 결제가 결정될 때 할 것
    private UUID dutchPayId;
    private UUID orderId;
    private UUID merchantId;
    private String categoryId;
    private CardSelectionType cardSelectionType;
    private long totalPrice;
    private UUID memberId;
    private RecommendType recommendType;
    private String cardNumber;
    private String cvc;
}
