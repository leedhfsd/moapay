package com.moa.payment.domain.analysis.service;

import com.moa.payment.domain.analysis.model.dto.CardHistoryPaymentLogDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryRequestDto;
import com.moa.payment.domain.analysis.model.dto.CardHistoryResponseDto;
import com.moa.payment.domain.analysis.repository.PaymentLogQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.moa.payment.domain.analysis.entity.Analysis;
import com.moa.payment.domain.analysis.entity.Gender;
import com.moa.payment.domain.analysis.model.dto.getMemberResponseDto;
import com.moa.payment.domain.analysis.repository.AnalysisRepository;
import com.moa.payment.domain.charge.entity.PaymentLog;
import com.moa.payment.domain.charge.repository.PaymentLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

	private final PaymentLogRepository paymentLogRepository;
	private final AnalysisRepository analysisRepository;
	private final PaymentLogQueryRepository paymentLogQueryRepository;
	private final RestTemplate restTemplate;

	@Value("${external-url.member}")
	String memberUrl;
	@Value("${external-url.core}")
	String coreUrl;

	//[0][X]은 남자, [1][X]은 여자
	//[X][0]은 0대, [X][1]은 10대, [X][2]은 20대, ..., [X][12]은 120대
	//[X][X][0]은 amount, [X][X][1]은 memberCount, [X][X][2]는 benefit
	private Long[][][] save = new Long[2][13][3];
	private HashSet<String> memberNum;

	public static int calculateAge(LocalDate birthDate) {
		// 현재 날짜 가져오기
		LocalDate currentDate = LocalDate.now();

		// Period 클래스를 사용해 두 날짜의 차이 계산
		if ((birthDate != null) && (currentDate != null)) {
			return Period.between(birthDate, currentDate).getYears();
		} else {
			return 0; // 생년월일이 null일 경우 0을 반환
		}
	}

	//-----scheduling 완료
	@Override
	public void setAverage() {
		// save 배열 초기화 (null 값을 0L로 초기화)
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 13; j++) {
				for (int k = 0; k < 3; k++) {
					save[i][j][k] = 0L;  // 초기값 0L로 설정
				}
			}
		}

		memberNum = new HashSet<>();

		//저번달 결제 내역 가져오기
		List<PaymentLog> paymentLogList = paymentLogRepository.findAllFromLastMonth();
		for (PaymentLog paylog : paymentLogList) {
			Long amount = paylog.getAmount(); // 결제한 금액
			Long benefit = paylog.getBenefitBalance(); //받은 혜택 금액
			UUID cardId = paylog.getCardId(); //사용한 카드 아이디
			//CardId로 myCard불러오고 여기서 memberId 뽑아내고
			UUID memberId = getMemberId(cardId);
			//memberId로 member 뽑아내서 나이, 성별, 핸드폰 번호 추출
			getMemberResponseDto member = getMemberInfo(memberId);

			int age = calculateAge(member.getBirthDate());
			int ageRange = age / 10; //연령대
			if (member.getGender().equals("M")) { //남자면 [0]
				save[0][ageRange][0] += amount;
				save[0][ageRange][2] += benefit;
				if (memberNum.add(member.getPhoneNumber())) { //새로 추가
					save[0][ageRange][1] += 1;
				}
			} else { //여자면 [1]
				save[1][ageRange][0] += amount;
				save[1][ageRange][2] += benefit;
				if (memberNum.add(member.getPhoneNumber())) { //새로 추가
					save[1][ageRange][1] += 1;
				}
			}

		}

		// 현재 날짜 가져오기
		LocalDate currentDate = LocalDate.now();

		// 전달 날짜 계산
		LocalDate previousMonthDate = currentDate.minusMonths(1);

		// 전달의 month와 year 출력
		int previousMonth = previousMonthDate.getMonthValue(); // 월
		int previousYear = previousMonthDate.getYear(); // 년

		for (int i = 0; i < 2; i++) {
			Gender gender;
			if (i == 0)
				gender = Gender.MALE;
			else
				gender = Gender.FEMALE;
			for (int j = 0; j < 13; j++) {
				Analysis analysis = Analysis.builder()
					.month(previousMonth)
					.year(previousYear)
					.gender(gender)
					.generation(j * 10 + "")
					.totalAmount(save[i][j][0])
					.userCount(save[i][j][1])
					.totalBenefit(save[i][j][2])
					.build();

				analysisRepository.save(analysis);
			}
		}

	}

	//paymentlog의 cardId에서 member가져오기
	public UUID getMemberId(UUID cardId) {
		String url = coreUrl + "/card/getMemberId";

		// POST 요청으로 cardId를 보내고, UUID로 응답을 받음
		ResponseEntity<UUID> response = restTemplate.postForEntity(url, cardId, UUID.class);

		return response.getBody();  // 응답에서 UUID를 반환
	}

	@Override
	public getMemberResponseDto getMemberInfo(UUID memberId) {
		try {
			String url = memberUrl + "/getMember";

			// POST 요청으로 memberId를 보내고, getMemberResponseDto로 응답 받음
			ResponseEntity<getMemberResponseDto> response = restTemplate.postForEntity(url, memberId,
				getMemberResponseDto.class);

			if (response.getBody() == null) {
				throw new RuntimeException("Received null response body for member ID: " + memberId);
			}

			return response.getBody();
		} catch (Exception e) {
			// 예외 처리
			throw new RuntimeException("Failed to get member info", e);
		}
	}

	//-------

	@Override
	public Long average(UUID memberId) {
		getMemberResponseDto member = getMemberInfo(memberId);
		int age = calculateAge(member.getBirthDate()); //나이 ex) 26살
		String ageGeneration = age / 10 * 10 + ""; //연령대 ex) 20대
		long avg = 0; //소비평균
		String g = member.getGender();
		Gender gender; //성별
		if (g.equals("M"))
			gender = Gender.MALE;
		else
			gender = Gender.FEMALE;

		// 현재 날짜 가져오기
		LocalDate currentDate = LocalDate.now();

		// 전달 날짜 계산
		LocalDate previousMonthDate = currentDate.minusMonths(1);

		// 전달의 month와 year 출력
		int previousMonth = previousMonthDate.getMonthValue(); // 월
		int previousYear = previousMonthDate.getYear(); // 년

		Analysis analysis = analysisRepository.findByPreviousMonthAndGenderAndGeneration(previousYear, previousMonth,
			gender, ageGeneration);
		avg = analysis.getTotalAmount() / analysis.getUserCount(); //평균구하기
		return avg;
	}

	@Override
	public CardHistoryResponseDto getCardHistory(CardHistoryRequestDto dto) {
		log.info("get card history - year : {}, month : {}", dto.getYear(), dto.getMonth());
		Month month = Month.of(dto.getMonth());
		YearMonth dateInfo = YearMonth.of(dto.getYear(), dto.getMonth());
		int lastDay = dateInfo.atEndOfMonth().lengthOfMonth();
		LocalDateTime startTime = LocalDateTime.of(dto.getYear(), dto.getMonth(), 1, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(dto.getYear(), dto.getMonth(), lastDay, 23, 59);
		List<CardHistoryPaymentLogDto> paymentLogs = paymentLogQueryRepository.findPaymentLog(dto.getCardId(), startTime, endTime);
		long totalAmount = 0;
		long totalBenefit = 0;
		for(CardHistoryPaymentLogDto log : paymentLogs){
			totalAmount += log.getAmount();
			totalBenefit += log.getBenefitBalance();
		}
		return CardHistoryResponseDto.builder()
				.cardId(dto.getCardId())
				.year(dto.getYear())
				.month(dto.getMonth())
				.totalAmount(totalAmount)
				.totalBenefit(totalBenefit)
				.paymentLogs(paymentLogs)
				.build();
	}
}
