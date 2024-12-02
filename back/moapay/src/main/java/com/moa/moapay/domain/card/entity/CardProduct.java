package com.moa.moapay.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.moapay.domain.card.model.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="card_product")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class CardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @NotNull
    @Column(name = "name", length = 100)
    private String name;

    @NotNull
    @Column(name = "company_name", length = 30)
    private String companyName;

    @ColumnDefault("9223372036854775807") // 기본값 어떻게 할까?
    @Column(name = "benefit_total_limit")
    private Long benefitTotalLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CardType type;

    @ColumnDefault(value = "0")
    @Column(name = "annual_fee")
    private Long annualFee;

    @ColumnDefault(value = "0")
    @Column(name = "annual_fee_foreign")
    private Long annualFeeForeign;

    @ColumnDefault(value = "0")
    @Column(name = "performance")
    private Long performance;

    @Column(name = "image_url", length = 200)
    private String imageUrl;

    // 혜택과의 관계 설정 (CardBenefit과의 일대다 관계)
    @OneToMany(mappedBy = "cardProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CardBenefit> benefits;

    private void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }

}
