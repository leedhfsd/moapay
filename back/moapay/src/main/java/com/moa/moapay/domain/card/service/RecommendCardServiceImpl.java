package com.moa.moapay.domain.card.service;

import com.moa.moapay.domain.card.entity.CardBenefit;
import com.moa.moapay.domain.card.entity.CardBenefitCategory;
import com.moa.moapay.domain.card.entity.CardProduct;
import com.moa.moapay.domain.card.entity.MyCard;
import com.moa.moapay.domain.card.model.*;
import com.moa.moapay.domain.card.model.dto.*;
import com.moa.moapay.domain.card.repository.CardBenefigCategoryRepository;
import com.moa.moapay.domain.card.repository.CardProductRepository;
import com.moa.moapay.domain.card.repository.MyCardQueryRepository;
import com.moa.moapay.domain.card.repository.MyCardRepository;
import com.moa.moapay.domain.generalpay.model.vo.PaymentCardInfoVO;
import com.moa.moapay.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendCardServiceImpl implements RecommendCardService {

    private final CardProductRepository productRepository;
    private final CardBenefigCategoryRepository benefigCategoryRepository;
    private final MyCardRepository myCardRepository;
    private final MyCardQueryRepository myCardQueryRepository;
    private final RestClient restClient;

    @Value("${external-url.payment}")
    private String paymentUrl;

    /**
     * 카드 상품 추천 로직
     *
     * @param request
     * @return
     */
    @Override
    public RecommendCardResponseDto recommendCard(UUID memberId) {

        List<MyCard> myCards = myCardRepository.findAllByMemberId(memberId);

        List<UUID> cardId = new ArrayList<>();

        for (MyCard myCard : myCards) {
            log.info(myCard.toString());
            cardId.add(myCard.getUuid());
        }

        RecomendCardToPayementDto recomendCardToPayementDto = RecomendCardToPayementDto.builder().cardId(cardId).build();
        // TODO: 1. 소비 패턴 분석 자료 가져오기
        ResponseEntity<GetPaymentLogWrapper> responseEntity = restClient.post()
                .uri(paymentUrl + "/charge/getPaymentLog")
                .contentType(MediaType.APPLICATION_JSON)
                .body(recomendCardToPayementDto)
                .retrieve()
                .toEntity(GetPaymentLogWrapper.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info(responseEntity.getBody().toString());
            GetPaymentLogResponseDto response = responseEntity.getBody().getData();

            Map<String, Long> categoryCountMap = response.getCategoryCountMap();

            // 필요 데이터 조회
            List<CardBenefitCategory> allCategory = benefigCategoryRepository.findAll();
            List<CardProduct> allProducts = productRepository.findAllWithBenefits();

            log.info("allProducs size {}", allProducts.size());

            // 점수 계산 (여기에 소비패턴 분석정보 같이 넘겨주기)
            List<CardProduct> recomendCards = calculateScore(allProducts, categoryCountMap);

            // 4. 제일 점수가 높은 카드 반환
            List<RecomendCardInfoResponseDto> recomendCardDtos = recomendCards.stream()
                    // 점수 계산된 카드를 반복문 돌면서
                    .map(cardProduct -> {
                        // 혜택 정보를 가지고
                        List<CardBenefit> benefits = cardProduct.getBenefits();
                        List<CardBenefitDto> benefitDtos = benefits.stream()
                                .map(benefit -> {
                                    // 카테고리 id가지고 카테고리 이름으로 바꾸기 (이것도 join으로 하면 될거 같긴 한데..)
                                    String categoryName = allCategory.stream()
                                            .filter(category -> Objects.equals(category.getId(), benefit.getCardBenefitCategory().getId())) // categoryId 비교
                                            .map(CardBenefitCategory::getName)
                                            .findFirst()
                                            .orElse("Unknown Category");
                                    // 그냥 엔티티를 반환 할 수 도 있지만 나중에 유지보수를 위해 DTO 생성후 리턴
                                    return CardBenefitDto.builder()
                                            .categoryName(categoryName)
                                            .benefitUnit(benefit.getBenefitUnit())
                                            .benefitDesc(benefit.getBenefitDesc())
                                            .benefitValue(benefit.getBenefitValue())
                                            .benefitType(benefit.getBenefitType())
                                            .benefitPoint(benefit.getBenefitPoint())
                                            .build();
                                }).collect(Collectors.toList());

                        // 최종 DTO 생성 후 리턴
                        return RecomendCardInfoResponseDto.builder()
                                .cardProductUuid(cardProduct.getUuid())
                                .cardProductName(cardProduct.getName())
                                .cardProductCompanyName(cardProduct.getCompanyName())
                                .cardProductPerformance(cardProduct.getPerformance())
                                .cardProductType(cardProduct.getType())
                                .cardProductAnnualFeeForeign(cardProduct.getAnnualFeeForeign())
                                .cardProductAnnualFee(cardProduct.getAnnualFee())
                                .cardProductImgUrl(cardProduct.getImageUrl())
                                .cardProductBenefitTotalLimit(cardProduct.getBenefitTotalLimit())
                                .cardBenefits(benefitDtos)
                                .build();
                    }).collect(Collectors.toList());

            return RecommendCardResponseDto.builder()
                    .categoryUsage(categoryCountMap)
                    .recommend(recomendCardDtos)
                    .build();
        }

        log.info("결제내역 불러오는 중 오류");
        throw new BusinessException(HttpStatus.NOT_FOUND, "결제내역 불러오는 중 오류");
    }

    @Override
    public List<PaymentCardInfoVO> recommendPayCard(UUID memberId, String categoryId, RecommendType recommendType, long totalPrice) {
        // 추천 카드 결제는 1000원 이상인 결제금액만 가능하며, 1원단위의 금액이 있으면 안된다
        if (totalPrice < 1000 || totalPrice % 10 != 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "추천 결제에 부적절한 결제 금액입니다.");
        }
        List<MyCard> cardList = myCardQueryRepository.findAllByMemberIdWithBenefits(memberId);
        if (cardList == null || cardList.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "요청한 회원이 소유한 카드가 없습니다.");
        }
        log.info("recommend pay card");
        // 실적형 - 혜택형 모두 사용혜택을 기준으로 정산하되, 실적형의 경우 실적이 넘치는 경우 모두 잘라낸다.
        // 분배 최소 금액 1000원, 최소 단위 10원 (상도덕이 있지...)
        PayCardScore[] scoreList = new PayCardScore[cardList.size()];
        for (int i = 0; i < cardList.size(); ++i) {
            // 카드 리스트별로 스코어를 계산 후, 객체를 생성해준다.
            PayCardScore score = getScore(cardList.get(i), categoryId, totalPrice);
            log.info("myCard score : {}", score.toString());
            scoreList[i] = score;
        }
        Arrays.sort(scoreList); // 전부 구했다면 원금 기준 혜택금액이 높은 순으로 정렬
        log.info("benefit score calculated");
        log.info("benefit score : {}", Arrays.toString(scoreList));
        // 응답에 사용할 리스트 생성
        List<PaymentCardInfoVO> recommendList;
        if (recommendType == RecommendType.BENEFIT) {
            log.info("recommend type : BENEFIT");
            // [1] 혜택형
            // 소지한 카드들 중 전체 금액을 기준으로 받을 수 있는 혜택 가장 높은 카드를 3개 꼽는다
            // 가장 높은 할인율을 보이는 카드부터 결제하되, 혜택금액 한도에 도달할 경우 다음 카드로 넘어간다
            // 각 카드별로 혜택을 받을 수 있는 최대 결제 금액을 기록해두는 게 좋을듯함.
            // 그래도 결제금액이 남는다면 실적을 채워주는 느낌으로 분배해준다.
            // 실적도 다 채웠다면... 요청에서 받았던 주 카드로 결제를 진행한다.
            recommendList = recommendByBenefit(scoreList, totalPrice, false);
        } else {
            log.info("recommend type : PERFORM");
            // [2] 실적형
            // 실적을 다 채우지 못한 카드들을, 결제 원금 기준으로 혜택 많이 받는 순으로 정렬한다.
            // 앞에서부터 3개를 집어 금액을 분배하되, 3개를 이용해 실적을 채울 수 없는 경우, 가장 남은 실적이 적게 남은 녀석부터 제외하고 다음 우선순위인 카드를 채운다.
            // 만일 모든 카드의 남은 실적 총합이 금액 총합보다 적다면, 혜택이 가장 큰 카드인 맨 앞 카드에 결제금을 몰아준다
            // todo : 혜택 관계없이 실적형의 경우 주 카드 먼저 긁히게 해야하는건가??
            recommendList = recommendByPerformance(scoreList, totalPrice, true);
        }
        return recommendList;
    }

    /**
     * 소비패턴에 따른 사용자 카드 상품 추천부
     * 하지만 소비패턴 가중치가 지금 없어서 더미데이터 필요해요~~
     *
     * @param allProducts
     * @return
     */
    private List<CardProduct> calculateScore(List<CardProduct> allProducts, Map<String, Long> categoryCountMap) {
        Map<CardProduct, Float> cardScoreMap = new HashMap<>();

        // 각 카드의 점수를 계산
        for (CardProduct cardProduct : allProducts) {
            float score = 0;

            // 각 카드의 혜택별 점수 계산
            for (CardBenefit cardBenefit : cardProduct.getBenefits()) {
                String cardCategory = cardBenefit.getCardBenefitCategory().getId();

                Long logs = categoryCountMap.get(cardCategory);

                int benefitPoint = cardBenefit.getBenefitPoint();
                // TODO : 가중치 계산
                score += (benefitPoint * logs);
            }

            // 전월 실적, 연회비 계산
            float performanceScore = performance(cardProduct);
            score += performanceScore;

            // 최종 점수를 맵에 저장
            cardScoreMap.put(cardProduct, score);
        }

        // 점수를 기준으로 카드 상품 정렬 (내림차순)
        List<Map.Entry<CardProduct, Float>> sortedCardScores = cardScoreMap.entrySet().stream()
                .sorted(Map.Entry.<CardProduct, Float>comparingByValue().reversed())
                .collect(Collectors.toList());

        // 최종 추천할 카드 리스트
        List<Optional<CardProduct>> cardProducts = new ArrayList<>();

        // 추천할 카드 상품 최대 10개 조회 (sortedCardScores 리스트에서 0부터 10번째 요소까지 슬라이싱)
        List<CardProduct> topCardProducts = sortedCardScores.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .subList(0, Math.min(9, sortedCardScores.size()));

        return topCardProducts;
    }

    /**
     * 전월실적, 연회비 점수
     *
     * @param cardProduct
     * @return
     */
    private float performance(CardProduct cardProduct) {

        Long performance = cardProduct.getPerformance();
        Long annualFee = cardProduct.getAnnualFee();

        int score = 0;

        if (performance <= 300000) {
            score -= 0;
        } else if (performance <= 500000) {
            score -= 1;
        } else if (performance <= 800000) {
            score -= 2;
        } else {
            score -= 3;
        }

        if (annualFee <= 50000) {
            score -= 0;
        } else if (annualFee <= 80000) {
            score -= 1;
        } else if (annualFee <= 150000) {
            score -= 2;
        } else {
            score -= 3;
        }

        return score;
    }

    private PayCardScore getScore(MyCard myCard, String categoryId, long totalPrice) {
        CardProduct product = myCard.getCardProduct();
        long maxBenefitAmount = 0;
        double benefitValue = 0;
        long maxPerformanceAmount = 0;
        double performanceValue = 0;
        List<CardBenefit> benefitList = product.getBenefits();
        List<CardBenefit> validBenefitList = new ArrayList<>();
        log.info("get valid benefit at {}", myCard.getCardProduct().getName());
        boolean hasBenefit = false;
        for (CardBenefit benefit : benefitList) { // 유효 혜택 계산
            String cardCategory = benefit.getCardBenefitCategory().getId();
            if (cardCategory.equals(categoryId) || cardCategory.equals("C0000")) { // 혜택타입 all인 경우에도 적용되어야 함
//                log.info("valid benefit -> {}", benefit.getBenefitDesc());
                hasBenefit = true;
                validBenefitList.add(benefit);
            }
        }
        log.info("benefit calculate has ended");
        log.info("valid benefit : {}", validBenefitList.size());
        // [1] 혜택 계산
        if (myCard.isPerformanceFlag() && hasBenefit) {
            // 전월실적을 충족하지 못해서 혜택을 못 받거나, 혜택 한도에 도달한 경우 받을 수 있는 혜택은 없음
            // 헤택을 받을 수 있다면, 0과 totalPrice를 대상으로 이분탐색을 시행
            long left = -1;
            long right = totalPrice + 1;
            // 혜택이 무한인 경우 고려해!!!
            boolean isBenefitInfinite = false;
            if (product.getBenefitTotalLimit() == 0) {
                isBenefitInfinite = true;
            }
            double remainedBenefit = product.getBenefitTotalLimit() - myCard.getBenefitUsage();
            double maxBenefit = 0; // 현재까지의 최대 헤택
            boolean isReached = false;
            while (left + 1 < right) {
                long mid = (left + right) / 2;
                BenefitInfo benefitInfo = calculateTotalBenefit(myCard, validBenefitList, categoryId, mid);
                double thisBenefit = benefitInfo.getTotalBenefit();
                // 이번 실적이 remainedBenefit 이상인 경우, 최고점에 도달했다는 뜻
                // 왼쪽 구역을 살펴봐야 한다
                if (!isBenefitInfinite && (long) (thisBenefit) >= remainedBenefit) {
                    log.info("reached remainedBenefit : {} >= {}", thisBenefit, remainedBenefit);
                    isReached = true;
                    right = mid;
                } else if (thisBenefit > maxBenefit) {
                    // 최고점에 도달하지 않았으며, 기존에 기록된 혜택보다 더 혜택을 받았다면 더 큰 범위를 봐야한다
                    maxBenefit = thisBenefit;
                    left = mid;
                } else {
                    // 최고점에 도달하지 못했으며, 기존값도 갱신하지 못했다면 더 큰 범위를 봐야 함
                    left = mid;
                }
//                    if (thisBenefit > maxBenefit) {
//                        // 더 큰 혜택값을 찾았다면, 탐색구역을 오른쪽으로 이동
////                        log.info("maxBenefit renewed : {} ->  {}", maxBenefit, thisBenefit);
//                        maxBenefit = thisBenefit;
////                        log.info("left moved : {} -> {}", left, mid);
//                        left = mid;
//                    } else {
//                        // 찾지 못했다면, 혜택한도에 도달한 것.
//                        // 왼쪽 구역을 살펴본다.
////                        log.info("right moved : {} -> {}", right, mid);
//                        right = mid;
//                    }
            }
            if (isReached) {
                maxBenefit = remainedBenefit;
                maxBenefitAmount = right;
            } else {
                maxBenefitAmount = left;
            }
            log.info("calculate benefit - left : {}, right : {}, maxBenefit : {}", left, right, maxBenefit);
            // 이분탐색 종료
            // 최종값은 left가 들고있게 된다
            benefitValue = maxBenefit;
        }
        // [2] 실적 계산
        // 만일 이미 실적을 채웠다면, 더이상 채울 수 있는 실적은 없다.
        if (myCard.getAmount() < product.getPerformance()) {
            long left = -1;
            long right = totalPrice + 1;
            double remainedPerformance = product.getPerformance() - myCard.getAmount();
            double maxPerformance = 0;
            boolean isReached = false;
            while (left + 1 < right) {
                long mid = (left + right) / 2;
                BenefitInfo benefitInfo = calculateTotalBenefit(myCard, validBenefitList, categoryId, mid);
                double thisPerformance = mid - benefitInfo.getTotalDiscount(); // 실제 실적 적용값은 원금에서 할인값을 뺀 만큼이다
                // 이번 실적이 remainedPerformance를 초과했거나 도달한 경우
                // 최고점에 도달했다는 뜻이므로, 왼쪽으로 살펴야 한다.
                if ((long) (thisPerformance) >= remainedPerformance) {
//                    log.info("reached remainedPerformance : {} >= {}", thisPerformance, remainedPerformance);
                    isReached = true;
                    right = mid;
                } else if (thisPerformance > maxPerformance) {
                    // 최고점에 도달하지 않았으며, 기존에 기록된 값보다 큰 실적을 냈다면 더 큰 범위를 봐야한다
                    maxPerformance = thisPerformance;
                    left = mid;
                } else {
                    // 최고점에 도달하지 못했으며, 기존값도 갱신하지 못했다면 더 큰 범위를 봐야 함
                    left = mid;
                }
            }
            if (isReached) { // 최고 금액에 도달한 적이 있다면 maxPerformance에 한계값 대입
                maxPerformance = remainedPerformance;
                maxPerformanceAmount = right;
            } else {
                maxPerformanceAmount = left;
            }
            log.info("calculate performance - left : {}, right : {}, maxPerformance : {}", left, right, maxPerformance);
//            maxPerformanceAmount = right;
            performanceValue = maxPerformance;
        }
        return PayCardScore.builder()
                .myCard(myCard)
                .maxBenefitAmount(maxBenefitAmount)
                .benefitValue(benefitValue)
                .maxPerformanceAmount(maxPerformanceAmount)
                .performanceValue(performanceValue)
                .build();
    }

    private BenefitInfo calculateTotalBenefit(MyCard myCard, List<CardBenefit> validBenefitList, String categoryId, long totalPrice) {
        CardProduct product = myCard.getCardProduct();
        double totalDiscount = 0;
        double totalPoint = 0;
        double totalCashback = 0;
        long benefitTotalLimit = product.getBenefitTotalLimit();
        boolean isBenefitInfinite = false;
        if (benefitTotalLimit == 0) { // 혜택한도가 0으로 표기된 경우, 혜택은 무한대로 적용된다
            isBenefitInfinite = true;
        }
        if (myCard.isPerformanceFlag()) { // 전월실적을 충족했어야 혜택 계산이 됨
            // 각 혜택별로 적용 정도와 한도 확인
            long usableBenefit = benefitTotalLimit - myCard.getBenefitUsage();
            double discountPerValue = 0;
            double pointPerValue = 0;
            double cashbackPerValue = 0;
            for (CardBenefit cardBenefit : validBenefitList) { // 거의 대부분 카테고리 하나당 혜택 하나겠지만, 확장성을 고려하여 반복문 작성
//                log.info("{}", cardBenefit.getBenefitDesc());
                if (cardBenefit.getBenefitType() == BenefitType.DISCOUNT) {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        discountPerValue += cardBenefit.getBenefitValue();
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX && cardBenefit.getBenefitValue() < totalPrice) {
                        // 고정할인값이 원래 결제 값보다 높다면 적용 불가
                        totalDiscount += (long) cardBenefit.getBenefitValue();
                    }
                } else if (cardBenefit.getBenefitType() == BenefitType.POINT) {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        pointPerValue += cardBenefit.getBenefitValue();
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX) {
                        totalPoint += (long) cardBenefit.getBenefitValue();
                    }
                } else {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        cashbackPerValue += cardBenefit.getBenefitValue();
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX) {
                        totalCashback += (long) cardBenefit.getBenefitValue();
                    }
                }
            }
            // 혜택 계산 종료
            // 퍼센테이지 혜택값을 정산한다
            totalDiscount += totalPrice * (discountPerValue / 100);
            totalPoint += totalPrice * (pointPerValue / 100);
            totalCashback += totalPrice * (cashbackPerValue / 100);
            // 사용가능 혜택값과 현재 혜택값을 비교
            if (!isBenefitInfinite && usableBenefit < totalDiscount + totalPoint + totalCashback) {
                // 차감 우선순위 : 캐시백 -> 포인트 -> 할인
                double exceeded = totalDiscount + totalPoint + totalCashback - usableBenefit;
                double newTotalCashback = Math.max(totalCashback - exceeded, 0);
                exceeded -= (totalCashback - newTotalCashback);
                double newTotalPoint = Math.max(totalPoint - exceeded, 0);
                exceeded -= (totalPoint - newTotalPoint);
                double newTotalDiscount = Math.max(totalDiscount - exceeded, 0);
                exceeded -= (totalDiscount - newTotalDiscount);
                // 바뀐 혜택을 원래 변수에 적용
                totalDiscount = newTotalDiscount;
                totalPoint = newTotalPoint;
                totalCashback = newTotalCashback;
            }
            // 최종적으로 결제될 금액을 정산
            // 만일 할인값이 원래 결제값보다 크다면 같게 조정해주어야 한다
            if (totalDiscount > totalPrice) {
                log.info("discount is bigger than amount : {} > {}", totalDiscount, totalPrice);
                totalDiscount = totalPrice;
            }
        }
//        log.info("discount, point, cashback : {}, {}, {}", totalDiscount, totalPoint,totalCashback);
        return BenefitInfo.builder()
                .totalDiscount(totalDiscount)
                .totalPoint(totalPoint)
                .totalCashback(totalCashback)
                .totalBenefit(totalDiscount + totalPoint + totalCashback)
                .build();
    }

    private List<PaymentCardInfoVO> recommendByBenefit(PayCardScore[] scoreList, long totalPrice, boolean looped) {
        List<PaymentCardInfoVO> recommendList = new ArrayList<>();
        if (scoreList[0].getBenefitValue() == 0) {
            // 가장 혜택이 큰 카드의 benefit value가 0이다 -> 혜택을 받을 수 있는 경우가 전혀 없다는 것
            log.info("받을 수 있는 혜택이 전혀 없음... 유감!");
            if (looped) {
                return recommendList;
            }
            return recommendByPerformance(scoreList, totalPrice, true);

        }
        long remainedPrice = totalPrice;
        // 후보군 3개를 뽑는다
        for (int i = 0; i < 3; ++i) {
            // 카드 추천은 최대 3개까지
            PayCardScore score = scoreList[i];
            log.info("this score : {}", score.toString());
            if (score.getBenefitValue() == 0) {
                log.info("there is no longer benefit");
                break; // 혜택을 받을 수 없는 카드만 남았다면 break
            }
            MyCard myCard = score.getMyCard();
            CardProduct product = myCard.getCardProduct();
            // 해당 카드로 얼마를 결제할지 결정
            long amount = 0;
            if (remainedPrice <= score.getMaxBenefitAmount()) {
                // 남은 값만큼 긁어도 혜택 한도를 초과하지 않는다면, 전부 긁는다
                amount = remainedPrice;
                remainedPrice = 0;
            } else {
                // 초과하는 경우, 혜택한도만큼 긁은 후 remainedPrice 삭감
                amount = score.getMaxBenefitAmount();
                if (amount % 10 != 0) {
                    // 1원단위 결제가 있다면 그 값은 반올림
                    long remained = amount % 10;
                    amount = amount + (10 - remained);
                }
                if (amount < 1000) { // 최소 결제 금액은 1000원이다
                    amount = 1000;
                }
                remainedPrice -= amount;
                if (remainedPrice < 1000) {
                    // 결제 최소 금액 이하로 남은 금액이 낮아지는 경우, 그냥 나머지도 이 카드로 긁는다
                    amount += remainedPrice;
                    remainedPrice = 0;
                }
            }
            recommendList.add(
                    PaymentCardInfoVO.builder()
                            .cardId(myCard.getUuid())
                            .cardName(product.getName())
                            .imageUrl(product.getImageUrl())
                            .cardNumber(myCard.getCardNumber())
                            .cvc(myCard.getCvc())
                            .amount(amount)
                            .usedAmount(myCard.getAmount())
                            .performance(product.getPerformance())
                            .benefitUsage(myCard.getBenefitUsage())
                            .build()
            );
            if (remainedPrice <= 0) { // 분배가 끝났다면 반복문 종료
                break;
            }
        }
        // 카드 3개 선정 완료
        // 혜택을 초과하는 결제금액이 남았다면, 남은 실적에 따라 앞 카드에서부터 분배한다
        if (remainedPrice > 0) {
            for (int i = 0; i < recommendList.size(); ++i) {
                PaymentCardInfoVO vo = recommendList.get(i);
                long remainedPerformance = vo.getPerformance() - vo.getUsedAmount(); // 넉넉하게 이번 결제 금액 제외하고 실적을 계산한다
                long newAmount = vo.getAmount();
                if (remainedPerformance > remainedPrice) {
                    newAmount += remainedPrice;
                    remainedPrice = 0;
                } else {
                    newAmount += remainedPerformance;
                    remainedPrice -= remainedPerformance;
                }
                vo.setAmount(newAmount);
                if (remainedPrice == 0) {
                    break;
                }
            }
        }
        // 이렇게 했는데도 금액이 남았다면 맨 앞 카드에 몰아준다.
        if (remainedPrice > 0) {
            PaymentCardInfoVO vo = recommendList.get(0);
            long newAmount = vo.getAmount() + remainedPrice;
            vo.setAmount(newAmount);
        }

        // 계산 종료
        // 이제 종합이 맞는지 확인
        log.info("recommend completed");
        long total = 0;
        for (PaymentCardInfoVO vo : recommendList) {
            log.info(vo.toString());
            total += vo.getAmount();
        }
        if (total != totalPrice) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "추천한 카드 종합과 결과가 맞지 않습니다. " + total + " / " + totalPrice);
        }
        return recommendList;
    }


    private List<PaymentCardInfoVO> recommendByPerformance(PayCardScore[] scoreList, long totalPrice, boolean looped) {
        List<PaymentCardInfoVO> recommendList = new ArrayList<>();
        long remainedPrice = totalPrice;
        for (int i = 0; i < scoreList.length; ++i) {
            // 카드 추천은 최대 3개까지
            if (recommendList.size() >= 3) {
                break;
            }
            PayCardScore score = scoreList[i];
            if (score.getPerformanceValue() == 0) { // 실적을 이미 다 채운 카드라면 pass
                continue;
            }
            MyCard myCard = score.getMyCard();
            CardProduct product = myCard.getCardProduct();
            // 채울 실적이 있다면 그 값을 우선하여 채운다
            long amount = 0;
            if (remainedPrice <= score.getMaxPerformanceAmount()) {
                // 전부 다 긁어도 모두 실적에 반영된다면, 전부 긁는다
                amount = remainedPrice;
                remainedPrice = 0;
            } else {
                // 반영되지 않는 값이 있다면, 한도만큼 긁은 후 remainedPrice 삭감
                amount = score.getMaxPerformanceAmount();
                if (amount % 10 != 0) {
                    // 1원단위 결제가 있다면 그 값은 반올림
                    long remained = amount % 10;
                    amount = amount + (10 - remained);
                }
                if (amount < 1000) { // 최소 결제 금액은 1000원이다
                    amount = 1000;
                }
                remainedPrice -= amount;
                if (remainedPrice < 1000) {
                    // 결제 최소 금액 이하로 남은 금액이 낮아지는 경우, 나머지도 이 카드로 결제
                    amount += remainedPrice;
                    remainedPrice = 0;
                }
            }
            recommendList.add(
                    PaymentCardInfoVO.builder()
                            .cardId(myCard.getUuid())
                            .cardName(product.getName())
                            .imageUrl(product.getImageUrl())
                            .cardNumber(myCard.getCardNumber())
                            .cvc(myCard.getCvc())
                            .amount(amount)
                            .usedAmount(myCard.getAmount())
                            .performance(product.getPerformance())
                            .benefitUsage(myCard.getBenefitUsage())
                            .build()
            );
            if (remainedPrice <= 0) {
                break;
            }
        }
        if (recommendList.isEmpty()) {
            // 이미 혜택으로 인해 계산한 결과가 없어서 실적으로 넘어온 거라면, 빈 배열 반환
            if (looped) {
                return recommendList;
            }
            // 그게 아니라면 혜택을 기반으로 카드 추천 시도
            return recommendByBenefit(scoreList, totalPrice, true);
        }
        // 이렇게 해도 결제 금액이 남았다면, 맨 앞 카드에 나머지 금액을 몰아준다
        if (remainedPrice > 0) {
            log.info("price remained : {}", remainedPrice);
            PaymentCardInfoVO vo = recommendList.get(0);
            long newAmount = vo.getAmount() + remainedPrice;
            vo.setAmount(newAmount);
        }

        // 계산 종료
        // 이제 종합이 맞는지 확인
        log.info("recommend completed");
        long total = 0;
        for (PaymentCardInfoVO vo : recommendList) {
            log.info(vo.toString());
            total += vo.getAmount();
        }
        if (total != totalPrice) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "추천한 카드 종합과 결과가 맞지 않습니다. " + total + " / " + totalPrice);
        }
        return recommendList;
    }
}
