package com.moa.payment.domain.charge.entity;

import com.moa.payment.domain.charge.model.ProcessingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "payment_log")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentLog {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 결제로그의 고유 id는 수정 불가

    @NotNull
    @Column(name = "card_id", columnDefinition = "binary(16)", nullable = false)
    private UUID cardId;

    @NotNull
    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @NotNull
    @Column(name = "cvc", nullable = false)
    private String cvc;

    @NotNull
    @Column(name = "amount")
    private long amount;

    @NotNull
    @Enumerated(value=EnumType.STRING)
    @Column(name="status")
    private ProcessingStatus status;

    @NotNull
    @Column(name = "merchant_id", columnDefinition = "binary(16)")
    private UUID merchantId;

    @NotNull
    @Column(name = "merchant_name", length=100)
    private String merchantName;

    @NotNull
    @Column(name = "category_id", columnDefinition = "char(5)")
    private String categoryId;

    @NotNull
    @Column(name = "benefit_balance")
    private long benefitBalance;

    @NotNull
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @NotNull
    @Column(name = "update_time")
    private LocalDateTime updateTime;

     @PrePersist
     private void prePersist() {
 //        this.uuid = Generators.timeBasedEpochGenerator().generate();
         LocalDateTime now = LocalDateTime.now();
         this.createTime = now;
         this.updateTime = now;
     }

     @PreUpdate
     private void preUpdate() {
         this.updateTime = LocalDateTime.now();
     }
// @PrePersist
// private void prePersist() {
//    this.createTime = getRandomNovemberDate();
//    this.updateTime = this.createTime;
// }
//
//    @PreUpdate
//    private void preUpdate() {
//        this.updateTime = getRandomNovemberDate();
//    }


    // private LocalDateTime getRandomNovemberDate() {
    //     // 2023년 11월 1일 00:00:00 ~ 2023년 11월 30일 23:59:59 사이의 랜덤한 날짜와 시간 생성
    //     int randomDay = ThreadLocalRandom.current().nextInt(1, 10); // 1 ~ 30 사이의 랜덤 날짜
    //     int randomHour = ThreadLocalRandom.current().nextInt(0, 24); // 0 ~ 23 사이의 랜덤 시간
    //     int randomMinute = ThreadLocalRandom.current().nextInt(0, 60); // 0 ~ 59 사이의 랜덤 분
    //     int randomSecond = ThreadLocalRandom.current().nextInt(0, 60); // 0 ~ 59 사이의 랜덤 초
    //
    //
    //     return LocalDateTime.of(2024, 9, randomDay, randomHour, randomMinute, randomSecond);
    // }
}