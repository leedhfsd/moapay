package com.moa.cardbank.domain.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "card_benefit_category")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CardBenefitCategory {
    @Id
    @Column(name = "id", columnDefinition = "char(5)")
    private String id;

    @Column(name = "name", length = 20)
    private String name;
}
