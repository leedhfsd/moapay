package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.CardBenefit;
import com.moa.cardbank.domain.card.entity.QCardBenefit;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    public List<CardBenefit> findCardBenefits(long productId, String categoryId) {
        QCardBenefit cardBenefit = QCardBenefit.cardBenefit;
        BooleanExpression productCondition = cardBenefit.productId.eq(productId);
        BooleanExpression categoryCondition = cardBenefit.categoryId.eq(categoryId)
                .or(cardBenefit.categoryId.eq("C0000"));
        return jpaQueryFactory.selectFrom(cardBenefit)
                .where(productCondition.and(categoryCondition))
                .fetch();
    }
}
