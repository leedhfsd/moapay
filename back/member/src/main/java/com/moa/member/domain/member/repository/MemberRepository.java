package com.moa.member.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moa.member.domain.member.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
	Optional<Member> findByPhoneNumber(String phoneNumber);
	Member findByName(String name);
	Optional<Member> findByUuid(UUID uuid);
	Member findByCredentialId(String CredentialId);
}
