package com.moa.payment.domain.analysis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @NotNull
    private String generation;

    @NotNull
    private int year;

    @NotNull
    private int month;

    @NotNull
    @Column(name = "total_amount")
    private long totalAmount;

    @NotNull
    @Column(name = "total_benefit")
    private long totalBenefit;

    @Column(name = "user_count")
    private long userCount;

    @NotNull
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }
}
