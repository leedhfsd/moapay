package com.moa.cardbank.domain.card.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.card.model.EarningType;
import com.moa.cardbank.domain.card.model.ProcessingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "earning_log")
@Getter
@Builder(toBuilder = true)
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EarningLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    // @ManyToOne으로 연결 : 결제 내역 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_log_id", insertable = false, updatable = false)
    private PaymentLog paymentLog;

    @Column(name = "payment_log_id")
    private Long paymentLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EarningType type;

    @Column(name = "amount")
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProcessingStatus status;

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
