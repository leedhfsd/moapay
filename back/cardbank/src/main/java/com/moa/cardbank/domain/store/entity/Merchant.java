package com.moa.cardbank.domain.store.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.account.entity.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @Column(name = "name", length = 50)
    private String name;

    // @ManyToOne으로 연결 : 계좌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @NotNull
    @Column(name = "account_id")
    private long accountId;

    // 카테고리 ID는 표기하되, 직접 참조관계로 삼지는 않는다
    @NotNull
    @Column(name = "category_id", columnDefinition = "char(5)")
    private String categoryId;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }

}
