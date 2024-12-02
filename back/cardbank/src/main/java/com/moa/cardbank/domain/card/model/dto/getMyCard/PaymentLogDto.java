package com.moa.cardbank.domain.card.model.dto.getMyCard;

import com.moa.cardbank.domain.card.model.ProcessingStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLogDto {
    private UUID paymentLogUuid;
    private UUID myCardUuid;
    private UUID merchantUuid;
    private long amount;
    private long discountAmount;
    private ProcessingStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
