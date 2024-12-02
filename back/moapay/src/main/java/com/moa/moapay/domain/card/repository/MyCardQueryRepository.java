package com.moa.moapay.domain.card.repository;

import com.moa.moapay.domain.card.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MyCardQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    /*
    @Query("SELECT mc FROM MyCard mc " +
       "JOIN FETCH mc.cardProduct cp " +
       "JOIN FETCH cp.benefits bf " +
       "JOIN FETCH bf.cardBenefitCategory bc")
    List<MyCard> findAllByMemberId(UUID memberId);
     */
    public List<MyCard> findAllByMemberIdWithBenefits(UUID memberId) {
        QMyCard myCard = QMyCard.myCard;
        QCardProduct cardProduct = QCardProduct.cardProduct;
        QCardBenefit cardBenefit = QCardBenefit.cardBenefit;
        QCardBenefitCategory benefitCategory = QCardBenefitCategory.cardBenefitCategory;

        return queryFactory
                .selectFrom(myCard)
                .join(myCard.cardProduct, cardProduct).fetchJoin()
                .join(cardProduct.benefits, cardBenefit).fetchJoin()
                .join(cardBenefit.cardBenefitCategory, benefitCategory).fetchJoin()
                .where(myCard.memberId.eq(memberId).and(myCard.cardStatus.eq(true)))
                .fetch();
    }

    public List<CardProduct> findAll() {
        QCardProduct cardProduct = QCardProduct.cardProduct;
        QCardBenefit cardBenefit = QCardBenefit.cardBenefit;
        QCardBenefitCategory benefitCategory = QCardBenefitCategory.cardBenefitCategory;

        return queryFactory
                .selectFrom(cardProduct)
                .join(cardProduct.benefits, cardBenefit).fetchJoin()
                .join(cardBenefit.cardBenefitCategory, benefitCategory).fetchJoin()
                .fetch();
    }

    public UUID findUuidByCardNumber(String cardNumber) {
        QMyCard myCard = QMyCard.myCard;
        return queryFactory
                .select(myCard.uuid)
                .from(myCard)
                .where(myCard.cardNumber.eq(cardNumber))
                .fetchOne();
    }

    public MyCard findByCardNumberFetchJoin(String cardNumber) {
        QMyCard myCard = QMyCard.myCard;
        QCardProduct cardProduct = QCardProduct.cardProduct;
        return queryFactory
                .selectFrom(myCard)
                .join(myCard.cardProduct, cardProduct).fetchJoin()
                .where(myCard.cardNumber.eq(cardNumber))
                .fetchOne();
    }

    public List<MyCard> findByMemberIdFetchJoin(UUID memberId) {
        QMyCard myCard = QMyCard.myCard;
        QCardProduct cardProduct = QCardProduct.cardProduct;
        QCardBenefit cardBenefit = QCardBenefit.cardBenefit;
        QCardBenefitCategory benefitCategory = QCardBenefitCategory.cardBenefitCategory;

        return queryFactory
                .selectFrom(myCard)
                .join(myCard.cardProduct, cardProduct).fetchJoin()
                .join(cardProduct.benefits, cardBenefit).fetchJoin()
                .where(myCard.memberId.eq(memberId))
                .fetch();
    }

    public List<UUID> findAllCardIdsByMemberId(UUID memberId) {
        QMyCard myCard = QMyCard.myCard;

        return queryFactory
            .select(myCard.uuid)
            .from(myCard)
            .where(myCard.memberId.eq(memberId))
            .fetch();
    }

    public void initialize() {
        QMyCard myCard = QMyCard.myCard;
        long count = queryFactory.update(myCard).set(myCard.amount, 0L).set(myCard.benefitUsage, 0L).execute();
        log.info("initialize moapay-MyCard -> count : {}", count);
        em.clear();
        em.flush();
    }
}
