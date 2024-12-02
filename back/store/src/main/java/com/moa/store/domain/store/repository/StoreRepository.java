package com.moa.store.domain.store.repository;

import java.util.Optional;
import java.util.UUID;

import com.moa.store.domain.store.model.Store;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
	Optional<Store> findByUuid(UUID uuid);
	void deleteByUuid(UUID uuid);
	boolean existsByUuid(UUID uuid);
}
