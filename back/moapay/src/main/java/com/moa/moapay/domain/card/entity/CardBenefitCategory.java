package com.moa.moapay.domain.card.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@Table(name = "card_benefit_category")
@NoArgsConstructor
@AllArgsConstructor
public class CardBenefitCategory {

    @Id
    @Column(name = "id", columnDefinition = "char(5)")
    private String id;

    @NotNull
    @Column(name = "name")
    private String name;

}
