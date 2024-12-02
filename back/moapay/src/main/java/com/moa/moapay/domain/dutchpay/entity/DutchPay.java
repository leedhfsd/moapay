package com.moa.moapay.domain.dutchpay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "dutch_pay")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutchPay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "uuid", unique = true)
    private UUID uuid;

    @NotNull
    @Column(name = "member_name")
    private String memberName;

    @NotNull
    @Column(name = "member_id")
    private UUID memberId;

    @NotNull
    @Column(name = "amount")
    private Long amount;

    @NotNull
    @Column(name = "is_manager")
    private boolean isManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status")
    private DutchStatus payStatus;

    // 부모 엔티티와의 ManyToOne 관계 설정 (DutchRoom과 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private DutchRoom roomEntity;

}
