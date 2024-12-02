package com.moa.cardbank.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.card.model.BenefitType;
import com.moa.cardbank.domain.card.model.BenefitUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.UUID;

@Entity
@Table(name = "card_benefit")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CardBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type")
    private BenefitType benefitType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_unit")
    private BenefitUnit benefitUnit;

    @NotNull
    @Column(name = "benefit_value", columnDefinition = "float(20, 2)")
    private double benefitValue;

    @Column(name = "benefit_desc", length = 300)
    private String benefitDesc;

    // @ManyToOne으로 연결 : 카드 상품 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private CardProduct product;

    @NotNull
    @Column(name = "product_id")
    private long productId;

    // 카테고리 ID는 표기하되, 직접 참조관계로 삼지는 않는다
    @NotNull
    @Column(name = "category_id", columnDefinition = "char(5)")
    private String categoryId;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }
}
