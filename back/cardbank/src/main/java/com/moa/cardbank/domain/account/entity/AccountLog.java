package com.moa.cardbank.domain.account.entity;

import com.fasterxml.uuid.Generators;
import com.moa.cardbank.domain.account.model.AccountLogType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_log")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "binary(16)", unique = true, nullable = false, updatable = false)
    private UUID uuid; // 고유 id는 수정 불가

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccountLogType type;

    // @ManyToOne으로 연결 : 계좌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @NotNull
    @Column(name = "account_id")
    private long accountId;

    @NotNull
    @Column(name = "value")
    private long value;

    @NotNull
    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "memo", length = 30)
    private String memo;

    @PrePersist
    public void prePersist() {
        this.uuid = Generators.timeBasedEpochGenerator().generate();
        this.time = LocalDateTime.now();
    }

}
