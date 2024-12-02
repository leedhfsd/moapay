package com.moa.payment.domain.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moa.payment.domain.statistics.entity.Statistics;

import java.util.Optional;
import java.util.UUID;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Optional<Statistics> findByMemberIdAndYearAndMonthAndCategoryId(UUID memberId, int year, int month, String categoryId);
}

