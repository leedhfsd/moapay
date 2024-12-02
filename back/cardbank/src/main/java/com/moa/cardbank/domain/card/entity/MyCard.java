package com.moa.cardbank.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.account.entity.Account;
import com.moa.cardbank.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "my_card")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @NotNull
    @Column(name = "card_number", length = 30)
    private String cardNumber;

    @NotNull
    @Column(name = "cvc", columnDefinition = "char(3)")
    private String cvc;

    @NotNull
    @Column(name = "performance_flag", columnDefinition = "tinyint(1)")
    private Boolean performanceFlag;

    @NotNull
    @Column(name = "card_limit")
    private long cardLimit;

    @NotNull
    @Column(name = "amount")
    private long amount;

    @NotNull
    @Column(name = "benefit_usage")
    private long benefitUsage;
    
    // @ManyToOne으로 연결 : 소유자, 대금계좌, 카드상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @NotNull
    @Column(name = "member_id")
    private long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @NotNull
    @Column(name = "account_id")
    private long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id",insertable = false, updatable = false)
    private CardProduct product;

    @NotNull
    @Column(name = "product_id")
    private long productId;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    private List<PaymentLog> paymentLog;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }
}
