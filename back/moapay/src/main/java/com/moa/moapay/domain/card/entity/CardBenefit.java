    package com.moa.moapay.domain.card.entity;

    import com.fasterxml.uuid.Generators;
    import com.moa.moapay.domain.card.model.BenefitType;
    import com.moa.moapay.domain.card.model.BenefitUnit;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.*;
    import org.hibernate.annotations.BatchSize;

    import java.util.UUID;

    @Entity
    @Table(name = "card_benefit")
    @Builder(toBuilder = true)
    @Getter
    @ToString
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @BatchSize(size = 100)
    public class CardBenefit {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private long id;

        @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
        private UUID uuid;

        @NotNull
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        private CardProduct cardProduct;

        @NotNull
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "category_id", referencedColumnName = "id")
        private CardBenefitCategory cardBenefitCategory;

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

        @Column(name = "benefit_point")
        private int benefitPoint;

        @PrePersist
        private void prePersist() {
            this.uuid = Generators.timeBasedEpochGenerator().generate();
        }
    }
