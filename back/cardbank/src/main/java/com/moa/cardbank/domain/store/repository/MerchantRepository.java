package com.moa.cardbank.domain.store.repository;

import com.moa.cardbank.domain.store.entity.Merchant;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends CrudRepository<Merchant, Long> {
    Optional<Merchant> findByUuid(UUID uuid);
    <S extends Merchant> S save(S merchant);
}
