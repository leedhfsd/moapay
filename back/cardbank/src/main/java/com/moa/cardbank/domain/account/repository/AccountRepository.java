package com.moa.cardbank.domain.account.repository;

import com.moa.cardbank.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    <S extends Account> S save(S account);
    Optional<Account> findByUuid(UUID uuid);
    Optional<Account> findByNumber(String Number);
}
