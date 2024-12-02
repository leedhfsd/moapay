package com.moa.cardbank.domain.account.repository;

import com.moa.cardbank.domain.account.entity.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {
    <S extends AccountLog> S save(S accountLog);
}
