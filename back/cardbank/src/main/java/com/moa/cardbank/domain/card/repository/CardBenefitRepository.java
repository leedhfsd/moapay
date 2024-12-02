package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.CardBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardBenefitRepository extends JpaRepository<CardBenefit, Long> {
    // S extends CardBenefit> List<S> saveAll(List<CardBenefit> cardBenefits);
}
