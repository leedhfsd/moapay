package com.moa.cardbank.domain.member.repository;

import com.moa.cardbank.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {
    <S extends Member> S save(S member);
    Optional<Member> findByUuid(UUID uuid);
}
