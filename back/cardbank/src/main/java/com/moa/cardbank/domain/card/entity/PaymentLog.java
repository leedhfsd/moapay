package com.moa.cardbank.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.card.model.ProcessingStatus;
import com.moa.cardbank.domain.store.entity.Merchant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_log")
@Getter
@DynamicInsert
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @NotNull
    @Column(name = "amount")
    private long amount;

    @ColumnDefault("0")
    @Column(name = "discount_amount")
    private Long discountAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProcessingStatus status;

    // @ManyToOne으로 연결 : 결제 카드, 가맹점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", updatable = false, insertable = false)
    private MyCard card;

    @NotNull
    @Column(name = "card_id")
    private long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", updatable = false, insertable = false)
    private Merchant merchant;

    @NotNull
    @Column(name = "merchant_id")
    private long merchantId;

    @NotNull
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.uuid = Generators.timeBasedEpochGenerator().generate();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

}
