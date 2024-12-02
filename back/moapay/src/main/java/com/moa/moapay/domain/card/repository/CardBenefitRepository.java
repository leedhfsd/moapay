package com.moa.moapay.domain.card.repository;

import com.moa.moapay.domain.card.entity.CardBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardBenefitRepository extends JpaRepository<CardBenefit, Long> {
    List<CardBenefit> findByCardProduct_Id(Long id);


    @Query("SELECT cb FROM CardBenefit cb join FETCH cb.cardProduct cp WHERE cp.uuid = :uuid")
    List<CardBenefit> findByCardProductUuid(UUID uuid);

}
