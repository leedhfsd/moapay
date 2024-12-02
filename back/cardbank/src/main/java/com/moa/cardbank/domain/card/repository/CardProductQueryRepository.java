package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.QCardProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CardProductQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    public Long getBenefitTotalLimitById(long id) {
        QCardProduct cardProduct = QCardProduct.cardProduct;
        return jpaQueryFactory
                .select(cardProduct.benefitTotalLimit)
                .from(cardProduct)
                .where(cardProduct.id.eq(id))
                .fetchOne();
    }
}
