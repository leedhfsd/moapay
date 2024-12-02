package com.moa.payment.domain.saving.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moa.payment.domain.saving.entity.Saving;

@Repository
public interface SavingRepository extends JpaRepository<Saving,Long> {
	Optional<Saving> findByMemberId(UUID memberId);
}
