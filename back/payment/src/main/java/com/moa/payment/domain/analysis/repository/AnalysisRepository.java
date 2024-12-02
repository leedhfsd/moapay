package com.moa.payment.domain.analysis.repository;

import java.util.List;

import com.moa.payment.domain.analysis.entity.Analysis;
import com.moa.payment.domain.analysis.entity.Gender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

	@Query("SELECT a FROM Analysis a " +
		"WHERE a.year = :year AND a.month = :month " +
		"AND a.gender = :gender AND a.generation = :generation")
	Analysis findByPreviousMonthAndGenderAndGeneration(
		@Param("year") int year,
		@Param("month") int month,
		@Param("gender") Gender gender,
		@Param("generation") String generation);


}
