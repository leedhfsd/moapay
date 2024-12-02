package com.moa.cardbank.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.card.model.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.UUID;

@Entity
@Table(name = "card_product")
@Getter
@Builder(toBuilder = true)
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CardProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @NotNull
    @Column(name = "name", length = 100)
    private String name;

    @NotNull
    @Column(name = "company_name", length = 30)
    private String companyName;

    @NotNull
    @Column(name = "benefit_total_limit")
    private long benefitTotalLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CardType type;

    @ColumnDefault("0")
    @Column(name = "annual_fee")
    private Long annualFee;

    @ColumnDefault("0")
    @Column(name = "annual_fee_foreign")
    private Long annualFeeForeign;

    @ColumnDefault("0")
    @Column(name = "performance")
    private long performance;

    @Column(name = "image_url", length = 200)
    private String imageUrl;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }
}
