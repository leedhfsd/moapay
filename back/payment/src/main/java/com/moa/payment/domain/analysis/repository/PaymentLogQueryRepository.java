package com.moa.payment.domain.analysis.repository;

import com.moa.payment.domain.analysis.model.dto.CardHistoryPaymentLogDto;
import com.moa.payment.domain.charge.entity.QPaymentLog;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PaymentLogQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<CardHistoryPaymentLogDto> findPaymentLog(UUID cardId, LocalDateTime start, LocalDateTime end) {
        QPaymentLog paymentLog = QPaymentLog.paymentLog;
        return queryFactory.select(Projections.fields(CardHistoryPaymentLogDto.class,
                        paymentLog.merchantName, paymentLog.benefitBalance, paymentLog.amount, paymentLog.categoryId, paymentLog.createTime))
                .from(paymentLog)
                .where(paymentLog.cardId.eq(cardId).and(paymentLog.createTime.between(start, end)))
                .orderBy(paymentLog.createTime.desc())
                .fetch();
    }

    public List<CardHistoryPaymentLogDto> findAllCardsPaymentLogs(List<UUID> cardIds, LocalDateTime start, LocalDateTime end) {
        for (UUID uuid: cardIds) {
            log.info("uuid: {}", uuid);
        }
        QPaymentLog paymentLog = QPaymentLog.paymentLog;
        return queryFactory.select(Projections.fields(CardHistoryPaymentLogDto.class,
                        paymentLog.cardId,
                        paymentLog.merchantName,
                        paymentLog.benefitBalance,
                        paymentLog.amount,
                        paymentLog.categoryId,
                        paymentLog.createTime))
                .from(paymentLog)
                .where(cardIdInExpression(cardIds)
                        .and(paymentLog.createTime.between(start, end)))
                .orderBy(paymentLog.createTime.desc())
                .fetch();
    }

    private BooleanExpression cardIdInExpression(List<UUID> cardIds) {
        QPaymentLog paymentLog = QPaymentLog.paymentLog;
        BooleanExpression booleanExpression = null;
        for (UUID cardId : cardIds) {
            BooleanExpression condition = paymentLog.cardId.eq(cardId);
            if (booleanExpression == null) {
                booleanExpression = condition;
            } else {
                booleanExpression = booleanExpression.or(condition);
            }
        }
        return booleanExpression;
    }
}
