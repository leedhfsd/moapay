package com.moa.cardbank.domain.card.service;

import com.moa.cardbank.domain.account.entity.Account;
import com.moa.cardbank.domain.account.model.dto.RefundByCardDto;
import com.moa.cardbank.domain.account.model.dto.RefundEarningDto;
import com.moa.cardbank.domain.account.model.dto.WithdrawByDebitCardDto;
import com.moa.cardbank.domain.account.repository.AccountRepository;
import com.moa.cardbank.domain.account.service.AccountService;
import com.moa.cardbank.domain.card.entity.*;
import com.moa.cardbank.domain.card.model.*;
import com.moa.cardbank.domain.card.model.dto.*;
import com.moa.cardbank.domain.card.model.dto.getMyCard.*;
import com.moa.cardbank.domain.card.repository.*;
import com.moa.cardbank.domain.member.entity.Member;
import com.moa.cardbank.domain.member.repository.MemberRepository;
import com.moa.cardbank.domain.store.entity.Merchant;
import com.moa.cardbank.domain.store.repository.MerchantRepository;
import com.moa.cardbank.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final AccountService accountService;
    private final MyCardRepository myCardRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final MerchantRepository merchantRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final EarningLogRepository earningLogRepository;
    private final CardProductRepository cardProductRepository;
    private final CardProductQueryRepository cardProductQueryRepository;
    private final CardBenefitRepository cardBenefitRepository;
    private final PaymentQueryRepository paymentQueryRepository;
    private final MyCardQueryRepository myCardQueryRepository;

    @Override
    @Transactional
    public CreateCardProductResponseDto createCardProduct(CreateCardProductRequestDto dto) {
        // 주어진 정보를 기반으로 카드 상품, 혜택 등록
        CardProduct cardProduct = CardProduct.builder()
                .name(dto.getName())
                .companyName(dto.getCompanyName())
                .benefitTotalLimit(dto.getBenefitTotalLimit())
                .type(dto.getType())
                .annualFee(dto.getAnnualFee())
                .annualFeeForeign(dto.getAnnualFeeForeign())
                .performance(dto.getPerformance())
                .imageUrl(dto.getImageUrl())
                .build();
        cardProductRepository.save(cardProduct);
        long productId = cardProduct.getId();
        List<CardBenefitDto> benefitList = dto.getBenefitList();
        List<CardBenefit> insertList = new ArrayList<>();
        for (CardBenefitDto benefitDto : benefitList) {
            insertList.add(
                    CardBenefit.builder()
                            .productId(productId)
                            .categoryId(benefitDto.getCategoryId())
                            .benefitType(benefitDto.getBenefitType())
                            .benefitUnit(benefitDto.getBenefitUnit())
                            .benefitValue(benefitDto.getBenefitValue())
                            .benefitDesc(benefitDto.getBenefitDesc())
                            .build()
            );
        }
        cardBenefitRepository.saveAll(insertList);
        return CreateCardProductResponseDto.builder()
                .cardId(cardProduct.getUuid())
                .build();
    }

    @Override
    @Transactional
    public ExecutePayResponseDto executePay(ExecutePayRequestDto dto) {
        // [1] 요청 카드 정보가 유효한지 검사
        log.info("요청 정보 : {}", dto.toString());
        UUID cardId = dto.getCardId();
        MyCard myCard = myCardRepository.findByUuid(cardId)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 정보입니다.1"));
        // UUID로 찾은 카드가 주어진 number, cvc와 일치하지 않으면 예외 출력
        if (!myCard.getCardNumber().equals(dto.getCardNumber()) || !myCard.getCvc().equals(dto.getCvc())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 정보입니다.2");
        }
        CardProduct product = myCard.getProduct();

        log.info("merchant Id = {}", dto.getMerchantId());

        Merchant merchant = merchantRepository.findByUuid(dto.getMerchantId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 정보입니다.3"));
        // [2] 결제 가능한 상태인지 확인
        // 카드 한도와 이번달 결제금액을 기반으로 남은 한도를 계산하고, 요청한 결제가 가능할지 확인

        // 우선 해당 카드가 결제하려는 가맹점 category와 일치하는 혜택이 있는지 확인
        // QueryRepository를 이용해 categoryId가 일치하는 혜택 entity를 불러온다
        long totalDiscount = 0;
        long totalPoint = 0;
        long totalCashback = 0;
        long benefitTotalLimit = product.getBenefitTotalLimit();
        boolean isBenefitInfinite = false;
        if(benefitTotalLimit == 0) { // 혜택한도가 0으로 표기된 경우, 혜택은 무한대로 적용된다
            log.info("this card has no benefit limit");
            isBenefitInfinite = true;
        }
        if (myCard.getPerformanceFlag()) { // 전월실적을 충족했어야 혜택 계산이 됨
            log.info("performance flag : TRUE -> calculate benefit value...");
            List<CardBenefit> benefitList = paymentQueryRepository.findCardBenefits(myCard.getProductId(), merchant.getCategoryId());
            // 각 혜택별로 적용 정도와 한도 확인
            long usableBenefit = benefitTotalLimit - myCard.getBenefitUsage();
            double discountPerValue = 0;
            double pointPerValue = 0;
            double cashbackPerValue = 0;
            for (CardBenefit cardBenefit : benefitList) { // 거의 대부분 카테고리 하나당 혜택 하나겠지만, 확장성을 고려하여 반복문 작성
                log.info("{}", cardBenefit.getBenefitDesc());
                // 문제 1 : 오차가 생각보다 빡세다...
                // 퍼센테이지 배율의 경우 다 합친 후에 곱하는 게 좋을지도?
                if (cardBenefit.getBenefitType() == BenefitType.DISCOUNT) {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        discountPerValue += cardBenefit.getBenefitValue();
                        //totalDiscount += (long)(dto.getAmount() * (cardBenefit.getBenefitValue()/100));
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX && cardBenefit.getBenefitValue() < dto.getAmount()) {
                        // 고정할인값이 원래 amount 값보다 높다면 적용 불가
                        totalDiscount += (long) cardBenefit.getBenefitValue();
                    }
                } else if (cardBenefit.getBenefitType() == BenefitType.POINT) {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        // totalPoint += (long)(dto.getAmount() * (cardBenefit.getBenefitValue()/100));
                        pointPerValue += cardBenefit.getBenefitValue();
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX) {
                        totalPoint += (long) cardBenefit.getBenefitValue();
                    }
                } else {
                    if (cardBenefit.getBenefitUnit() == BenefitUnit.PERCENTAGE) {
                        // totalCashback += (long)(dto.getAmount() * (cardBenefit.getBenefitValue()/100));
                        cashbackPerValue += cardBenefit.getBenefitValue();
                    } else if (cardBenefit.getBenefitUnit() == BenefitUnit.FIX) {
                        totalCashback += (long) cardBenefit.getBenefitValue();
                    }
                }
            }
            // 혜택 계산 종료
            // 퍼센테이지 혜택값을 정산한다
            totalDiscount += (long) (dto.getAmount() * (discountPerValue / 100));
            totalPoint += (long) (dto.getAmount() * (pointPerValue / 100));
            totalCashback += (long) (dto.getAmount() * (cashbackPerValue / 100));
            // 사용가능 혜택값과 현재 혜택값을 비교
            if (!isBenefitInfinite && usableBenefit < totalDiscount + totalPoint + totalCashback) {
                // 차감 우선순위 : 캐시백 -> 포인트 -> 할인
                long exceeded = totalDiscount + totalPoint + totalCashback - usableBenefit;
                long newTotalCashback = Math.max(totalCashback - exceeded, 0);
                exceeded -= (totalCashback - newTotalCashback);
                long newTotalPoint = Math.max(totalPoint - exceeded, 0);
                exceeded -= (totalPoint - newTotalPoint);
                long newTotalDiscount = Math.max(totalDiscount - exceeded, 0);
                exceeded -= (totalDiscount - newTotalDiscount);
                // 바뀐 혜택을 원래 변수에 적용
                totalDiscount = newTotalDiscount;
                totalPoint = newTotalPoint;
                totalCashback = newTotalCashback;
            }
        }
        log.info("discount, point, cashback : {}, {}, {}", totalDiscount, totalPoint, totalCashback);
        // 최종적으로 결제될 금액을 정산
        // 만일 할인값이 원래 결제값보다 크다면 같게 조정해주어야 한다
        if (totalDiscount > dto.getAmount()) {
            log.info("discount is bigger than amount : {} > {}", totalDiscount, dto.getAmount());
            totalDiscount = dto.getAmount();
        }
        long finalAmount = dto.getAmount() - totalDiscount; // 적립, 캐시백은 결제 금액을 할인해주는 게 아닌 추후 환급하는 느낌.
        log.info("final amount : {}", finalAmount);
        if (myCard.getCardLimit() < myCard.getAmount() + finalAmount) {
            // finalAmount만큼 결제했을 때 한도초과인 경우, 결제할 수 없다
            // 한도초과 응답을 전송
            log.info("MAXED OUT : {} + {} > {}", myCard.getAmount(), finalAmount, myCard.getCardLimit());
            return ExecutePayResponseDto.builder()
                    .merchantName(merchant.getName())
                    .categoryId(merchant.getCategoryId())
                    .status(PayStatus.MAXED_OUT)
                    .build();
        }

        // [3] 결제
        // 결제 자격이 충분하고 한도 초과도 아닌 경우, 결제 가능
        // 결제 로그를 남기고, 혜택 사용 내역과 관련된 값을 기록한다
        ProcessingStatus paymentStatus = ProcessingStatus.APPROVED;
        // 체크카드인 경우, 바로 출금되어야 하므로 통장잔고를 확인한 후 출금 처리한다.
        if (product.getType() == CardType.DEBIT && finalAmount > 0) {
            Account account = myCard.getAccount();
            if (account.getBalance() < finalAmount) { // 통장 잔고가 없는 경우, 실패
                return ExecutePayResponseDto.builder()
                        .merchantName(merchant.getName())
                        .categoryId(merchant.getCategoryId())
                        .status(PayStatus.OUT_OF_MONEY)
                        .build();
            }
            WithdrawByDebitCardDto withdrawDto = WithdrawByDebitCardDto.builder()
                    .accountId(account.getUuid())
                    .value(finalAmount)
                    .memo(merchant.getName())
                    .build();
            accountService.WithdrawByDebitCard(withdrawDto);
            // 체크카드인 경우, 추후 정산이 필요없으므로 settled로 표기
            paymentStatus = ProcessingStatus.SETTLED;
        }

        PaymentLog paymentLog = PaymentLog.builder()
                .cardId(myCard.getId())
                .merchantId(merchant.getId())
                .amount(finalAmount)
                .status(paymentStatus)
                .discountAmount(totalDiscount)
                .build();

        paymentLogRepository.save(paymentLog);

        // 저장에 성공했다면, myCard의 값을 변경
        long newAmount = myCard.getAmount() + finalAmount;
        long newBenefitUsage = myCard.getBenefitUsage() + totalDiscount + totalPoint + totalCashback;
        MyCard newCard = myCard.toBuilder()
                .amount(newAmount)
                .benefitUsage(newBenefitUsage)
                .build();
        myCardRepository.save(newCard);

        if (totalPoint > 0) {
            EarningLog earningLog = EarningLog.builder()
                    .paymentLogId(paymentLog.getId())
                    .type(EarningType.POINT)
                    .amount(totalPoint)
                    .status(ProcessingStatus.APPROVED)
                    .build();
            earningLogRepository.save(earningLog);
        }

        if (totalCashback > 0) {
            EarningLog earningLog = EarningLog.builder()
                    .paymentLogId(paymentLog.getId())
                    .type(EarningType.CASHBACK)
                    .amount(totalCashback)
                    .status(ProcessingStatus.APPROVED)
                    .build();
            earningLogRepository.save(earningLog);
        }
        BenefitDetailDto benefitDetailDto = BenefitDetailDto.builder()
                .discount(totalDiscount)
                .point(totalPoint)
                .cashback(totalCashback)
                .build();

        return ExecutePayResponseDto.builder()
                .merchantName(merchant.getName())
                .categoryId(merchant.getCategoryId())
                .status(PayStatus.APPROVED)
                .paymentId(paymentLog.getUuid())
                .amount(paymentLog.getAmount())
                .benefitActivated(myCard.getPerformanceFlag())
                .benefitBalance(totalDiscount + totalPoint + totalCashback)
                .remainedBenefit(Math.max(benefitTotalLimit - newBenefitUsage, 0)) // benefitTotalLimit가 0인 경우, 음수가 나올 수 있음
                .benefitDetail(benefitDetailDto)
                .build();
    }

    @Override
    @Transactional
    public CancelPayResponseDto cancelPay(CancelPayRequestDto dto) {
        // [1] 요청 카드 정보가 유효한지 검사
        PaymentLog paymentLog = paymentLogRepository.findByUuid(dto.getPaymentId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다."));
        if (paymentLog.getStatus() == ProcessingStatus.CANCELED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다.");
        }
        MyCard myCard = myCardRepository.findByUuid(dto.getCardId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다."));
        if (paymentLog.getCardId() != myCard.getId()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 정보입니다.");
        }
        if (!myCard.getCardNumber().equals(dto.getCardNumber()) || !myCard.getCvc().equals(dto.getCvc())) {
            // 카드 정보가 틀린 경우, 결제 취소 불가
            throw new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 정보입니다.4");
        }

        // 정보가 유효함을 확인했다면 결제 취소 진행
        // [2] 환불 처리
        // 이미 정산된 값이라면, 환불 처리 필요
        UUID accountId = myCard.getAccount().getUuid();
        String merchantName = paymentLog.getMerchant().getName();
        if (paymentLog.getStatus() == ProcessingStatus.SETTLED && paymentLog.getAmount() > 0) {
            log.info("payment status : settled -> processing refund...");
            RefundByCardDto refundDto = RefundByCardDto.builder()
                    .accountId(accountId)
                    .value(paymentLog.getAmount())
                    .memo(merchantName)
                    .build();
            accountService.RefundByCard(refundDto);
        }
        // [3] 결제 무효화
        // 적립과 결제의 status를 변경
        List<EarningLog> earningLogList = earningLogRepository.findByPaymentLogId(paymentLog.getId());
        long totalBenefit = 0; // 혜택값 복구를 위해 전체 혜택값 정산
        for (EarningLog earningLog : earningLogList) {
            totalBenefit += earningLog.getAmount();
            if (earningLog.getStatus() == ProcessingStatus.SETTLED) {
                // 정산된 적립금이나 캐시백이었다면, 다시 뺏어갈 필요가 있다
                String comment;
                if (earningLog.getType() == EarningType.POINT) {
                    comment = "포인트 ";
                } else comment = "캐시백 ";
                RefundEarningDto earningDto = RefundEarningDto.builder()
                        .accountId(accountId)
                        .value(earningLog.getAmount())
                        .memo(comment + "취소")
                        .build();
                accountService.RefundEarning(earningDto);
            }
            EarningLog newEarningLog = earningLog.toBuilder()
                    .status(ProcessingStatus.CANCELED)
                    .build();
            earningLogRepository.save(newEarningLog);
        }
        totalBenefit += paymentLog.getDiscountAmount(); // 할인값까지 종합
        PaymentLog newPaymentLog = paymentLog.toBuilder()
                .status(ProcessingStatus.CANCELED)
                .build();
        paymentLogRepository.save(newPaymentLog);

        // [4] 사용 혜택량 변경
        // 환불한만큼 사용 혜택 현황을 돌려놓는다. < 일단은 그냥 진행...
        // 단, 현재 혜택한도를 넘는 이상으로 돌려주지는 않는다
        long newAmount = myCard.getAmount() - newPaymentLog.getAmount(); // 사용금액은 취소된 결제금액만큼 차감
        long newBenefitUsage = Math.max(myCard.getBenefitUsage() - totalBenefit, 0); // 혜택 사용량도 차감해주나 0 미만으로 차감해주지는 않음
        MyCard newMyCard = myCard.toBuilder()
                .amount(newAmount)
                .benefitUsage(newBenefitUsage)
                .build();
        myCardRepository.save(newMyCard);

        return CancelPayResponseDto.builder()
                .amount(newPaymentLog.getAmount())
                .benefitBalance(totalBenefit)
                .remainedBenefit(cardProductQueryRepository.getBenefitTotalLimitById(newMyCard.getProductId()) - newMyCard.getBenefitUsage())
                .build();
    }

    @Override
    public CreateMyCardResponseDto createMyCard(CreateMyCardRequestDto dto) {
        Member member = memberRepository.findByUuid(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다.1"));
        Account account = accountRepository.findByUuid(dto.getAccountId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다.2"));
        CardProduct cardProduct = cardProductRepository.findByUuid(dto.getCardProductId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다.3"));
        // 카드 한도는 최소 1만원부터 시작하도록 함
        if (dto.getCardLimit() < 10000) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "한도가 너무 작습니다.");
        }
        // 카드번호와 cvc는 무작위 생성
        // 카드 번호는 중복검사 이후 결정한다
        String cardNumber;
        while (true) {
            cardNumber = String.valueOf((int) (Math.random() * 100000000)) + String.valueOf((int) (Math.random() * 100000000));
            if (!myCardRepository.existsByCardNumber(cardNumber)) { // 안 겹치는 카드 번호가 나올 때까지 반복
                break;
            }
        }
        String cvc = String.valueOf((int) (Math.random() * 900) + 100);

        MyCard newCard = MyCard.builder()
                .cardNumber(cardNumber)
                .cvc(cvc)
                .performanceFlag(false)
                .cardLimit(dto.getCardLimit())
                .amount(0)
                .benefitUsage(0)
                .memberId(member.getId())
                .accountId(account.getId())
                .productId(cardProduct.getId())
                .build();

        myCardRepository.save(newCard);

        return CreateMyCardResponseDto.builder()
                .myCardId(newCard.getUuid())
                .myCardNumber(newCard.getCardNumber())
                .cvc(newCard.getCvc())
                .build();
    }

    @Override
    public List<GetMyCardsResponseDto> getMyCards(GetMyCardsRequestDto getMyCardsRequestDto) {

        List<MyCard> myCards = myCardRepository.findByPhoneNumber(getMyCardsRequestDto.getPhoneNumber());

        List<GetMyCardsResponseDto> responseDtos = myCards.stream().map(
                myCard -> {
                    CardProductDto cardProductDto = CardProductDto
                            .builder()
                            .cardProductUuid(myCard.getProduct().getUuid())
                            .cardProductName(myCard.getProduct().getName())
                            .cardProductType(myCard.getProduct().getType())
                            .cardProductPerformance(myCard.getProduct().getPerformance())
                            .cardProductAnnualFee(myCard.getProduct().getAnnualFee())
                            .cardProductAnnualFeeForeign(myCard.getProduct().getAnnualFeeForeign())
                            .cardProductBenefitTotalLimit(myCard.getProduct().getBenefitTotalLimit())
                            .cardProductImgUrl(myCard.getProduct().getImageUrl())
                            .cardProductCompanyName(myCard.getProduct().getCompanyName())
                            .build();

                    AccountDto accountDto = AccountDto
                            .builder()
                            .accountNumber(myCard.getAccount().getNumber())
                            .accountUuid(myCard.getAccount().getUuid())
                            .balance(myCard.getAccount().getBalance())
                            .build();

                    return GetMyCardsResponseDto.builder()
                            .uuid(myCard.getUuid())
                            .cardNumber(myCard.getCardNumber())
                            .cvc(myCard.getCvc())
                            .benefitUsage(myCard.getBenefitUsage())
                            .performanceFlag(myCard.getPerformanceFlag())
                            .cardLimit(myCard.getCardLimit())
                            .accounts(accountDto)
                            .cardProduct(cardProductDto)
                            .amount(myCard.getAmount())
                            .build();
                }
        ).collect(Collectors.toList());

        return responseDtos;
    }

    @Override
    public CardRestResponseDto registration(CardRegistrationRequestDto registrationRequestDto) {

        MyCard myCard = myCardRepository.findByCardNumber(registrationRequestDto.getCardNumber());

        if(myCard == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "카드사에 등록된 나의 카드가 없습니다.");
        }

        CardRestResponseDto cardRestResponseDto = CardRestResponseDto
                .builder()
                .uuid(myCard.getUuid())
                .cardNumber(myCard.getCardNumber())
                .cvc(myCard.getCvc())
                .performanceFlag(myCard.getPerformanceFlag())
                .benefitUsage(myCard.getBenefitUsage())
                .cardProductUuid(myCard.getProduct().getUuid())
                .cardProductName(myCard.getProduct().getName())
                .cardProductCompanyName(myCard.getProduct().getCompanyName())
                .cardProductBenefitTotalLimit(myCard.getProduct().getBenefitTotalLimit())
                .cardProductType(String.valueOf(myCard.getProduct().getType()))
                .cardProductAnnualFee(myCard.getProduct().getAnnualFee())
                .cardProductAnnualFeeForeign(myCard.getProduct().getAnnualFeeForeign())
                .cardProductImgUrl(myCard.getProduct().getImageUrl())
                .accountUuid(myCard.getAccount().getUuid())
                .accountNumber(myCard.getAccount().getNumber())
                .balance(myCard.getAccount().getBalance())
                .amount(myCard.getAmount())
                .cardLimit(myCard.getCardLimit())
                .build();

        return cardRestResponseDto;
    }

    @Override
    @Transactional
    public void initialize() {
        myCardQueryRepository.initialize();
    }
}
