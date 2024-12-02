package com.moa.cardbank.domain.card.model.dto.getMyCard;

import com.moa.cardbank.domain.account.entity.Account;
import com.moa.cardbank.domain.card.entity.CardProduct;
import com.moa.cardbank.domain.card.entity.PaymentLog;
import com.moa.cardbank.domain.member.entity.Member;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetMyCardsResponseDto {
    private UUID uuid;
    private String cardNumber;
    private String cvc;
    private boolean performanceFlag;
    private long cardLimit;
    private long amount;
    private long benefitUsage;

    private CardProductDto cardProduct;
    private AccountDto accounts;
}
