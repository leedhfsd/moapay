package com.moa.moapay.domain.card.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.UUID;

@Entity
@Table(name="my_card")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@ToString
public class MyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", nullable = false, updatable = false)
    private UUID uuid;

    @NotNull
    @Column(name = "card_number", unique = true, length = 30)
    private String cardNumber;

    @NotNull
    @Column(name = "cvc", columnDefinition = "char(3)")
    private String cvc;

    @NotNull
    @Column(name = "performance_flag", columnDefinition = "tinyint(1)")
    private boolean performanceFlag;

    @NotNull
    @Column(name = "card_limit")
    private Long cardLimit;

    @NotNull
    @Column(name = "amount")
    private Long amount;

    @NotNull
    @Column(name = "benefit_usage")
    private Long benefitUsage;

    @NotNull
    @Column(name = "member_id",  columnDefinition = "binary(16)")
    private UUID memberId;

    @DefaultValue("true")
    @Column(name = "card_status")
    private Boolean cardStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private CardProduct cardProduct;
}
