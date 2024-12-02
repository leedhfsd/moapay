package com.moa.cardbank.domain.card.repository;

import com.moa.cardbank.domain.card.entity.QMyCard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MyCardQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public void initialize() {
        QMyCard myCard = QMyCard.myCard;
        long count = queryFactory.update(myCard).set(myCard.amount, 0L).set(myCard.benefitUsage, 0L).execute();
        log.info("initialize cardbank-MyCard -> count : {}", count);
        em.clear();
        em.flush();
    }
}
