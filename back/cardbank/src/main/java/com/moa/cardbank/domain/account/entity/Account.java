package com.moa.cardbank.domain.account.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    // @ManyToOne으로 연결 : 예금주 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "binary(16)", insertable = false, updatable = false)
    private Member member;

    @NotNull
    @Column(name = "member_id")
    private Long memberId;

    @NotNull
    @Column(name = "number", unique = true, updatable = false)
    private String number;

    @NotNull
    @Column(name = "balance")
    private Long balance;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
    }

}
