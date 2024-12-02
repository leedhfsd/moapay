package com.moa.cardbank.domain.card.model.dto.getMyCard;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private UUID memberId;
    private String memberName;
}
