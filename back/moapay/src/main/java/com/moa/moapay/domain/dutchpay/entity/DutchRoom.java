package com.moa.moapay.domain.dutchpay.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dutch_room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DutchRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid")
    private UUID uuid;

    @NotNull
    @Column(name = "order_uuid")
    private UUID orderId;

    @NotNull
    @Column(name = "merchant_uuid")
    private UUID merchantId;

    @NotNull
    @Column(name = "merchant_name")
    private String merchantName;

    @NotNull
    @Column(name = "category_id")
    private String categoryId;

    @NotNull
    @Column(name = "total_price")
    private long totalPrice;

    @DefaultValue("1")
    @Column(name = "max_person")
    private long maxPerson;

    @DefaultValue("0")
    @Column(name = "cur_person")
    private long curPerson;

    @NotNull
    @Column(name = "manager_id")
    private UUID managerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DutchStatus status;

    // 'roomEntity'는 DutchPay 엔티티에서 부모 엔티티와의 연관관계를 정의한 필드명
    @OneToMany(mappedBy = "roomEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DutchPay> dutchPayList;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }

    public void addDutchPay(DutchPay dutchPay) {
        dutchPay.setRoomEntity(this);  // 부모 엔티티를 자식 엔티티에 설정
        dutchPayList.add(dutchPay);
    }

    public void removeDutchPay(DutchPay dutchPay) {
        dutchPay.setRoomEntity(null);  // 자식 엔티티에서 부모 관계 제거
        dutchPayList.remove(dutchPay);
    }

    public void clearDutchPayList() {
        for (DutchPay dutchPay : dutchPayList) {
            dutchPay.setRoomEntity(null);  // 부모 엔티티에서 모든 자식과의 관계 해제
        }
        dutchPayList.clear();
    }

}

