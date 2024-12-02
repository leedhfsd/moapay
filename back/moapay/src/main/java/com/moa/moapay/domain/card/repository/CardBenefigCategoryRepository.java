package com.moa.moapay.domain.card.repository;

import com.moa.moapay.domain.card.entity.CardBenefitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBenefigCategoryRepository extends JpaRepository<CardBenefitCategory, Long> {

}
