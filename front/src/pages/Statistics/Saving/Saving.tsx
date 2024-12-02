import { useEffect, useState } from "react";
import { useAuthStore } from "../../../store/AuthStore";
import { useSavingStore } from "../../../store/SavingStore";
import {
  FirstStep,
  PreView,
  SecondStep,
  LastStep,
  AlramModal,
  Wrapper,
} from "./Saving.styles"; // LastStep 추가
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCaretLeft,
  faCaretRight,
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";
import { useLocation, useNavigate } from "react-router-dom";
import { PATH } from "../../../constants/path";
import SmallBarGraph from "../../../components/statistics/Chart/BarGraph/SmallBarGraph";
import axios from "axios";
import apiClient from "../../../axios";

interface savingDataType {
  memberId: string;
  todayAmount: number;
  limitAmount: number;
  amount: number;
  daily: number[];
}

const Saving = () => {
  const { id, accessToken } = useAuthStore();

  const location = useLocation();
  const calculatedPrice = location.state?.calculatedPrice; // 전달된 calculatedPrice 값

  const [goal, setGoal] = useState<number>(0);
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setGoal(Number(e.target.value)); // 입력값을 goal에 반영
  };

  const { name } = useAuthStore();

  const navigate = useNavigate();

  const { savingMode, savingAlram, setSavingAlram, setSavingMode } =
    useSavingStore();

  // 소비 챌린지 설정 스탭
  const [settingStep, setSettingStep] = useState<number>(1);

  //선택된 주
  const [selectedWeek, setSelectedWeek] = useState<number>(0);

  //서버에서 불러오는 데이터
  const [savingData, setSavingData] = useState<savingDataType>({
    memberId: "",
    todayAmount: 0,
    limitAmount: 0,
    amount: 0,
    daily: [],
  });

  // 해당 주차의 총 금액
  const [weekTotalAmount, setWeekTotalAmount] = useState<number>(0);
  //해당 주차의 금액 리스트 (일 ~ 월)
  const [weekAmountList, setWeekAmountList] = useState<number[]>([]);

  // 목표 설정 API 호출
  const settingGoal = async () => {
    try {
      const response = await apiClient.post(
        `/api/moapay/pay/saving/setLimit`,
        { memberId: id, limitAmount: goal * 10000 },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (response.status == 200) {
        setSettingStep(3);
      }
    } catch (e) {
      console.log(e);
    }
  };

  // 주차 계산 함수
  const getWeekOfMonth = () => {
    const firstDayOfMonth = new Date(
      new Date().getFullYear(),
      new Date().getMonth(),
      1
    );
    const firstDayWeekday = firstDayOfMonth.getDay(); // 0 (일요일) ~ 6 (토요일)

    // 현재 날짜
    const currentDay = new Date().getDate();

    // 첫 주의 첫날(월요일) 기준으로 현재 날짜가 몇 주째인지 계산
    const weekOffset = firstDayWeekday === 0 ? 7 : firstDayWeekday; // 첫 주의 일요일일 경우 보정
    const weekNumber = Math.ceil((currentDay + weekOffset - 1) / 7);

    return weekNumber;
  };

  // 주차 -> 한글로 변경
  const getNumberInKorean = (num: number = 1): string => {
    const koreanNumbers = ["첫", "둘", "셋", "넷", "다섯", "여섯"];
    return koreanNumbers[num - 1] || "";
  };

  // 데이터 긁어오기 + 데이터 정제
  const getSavingData = async () => {
    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/saving/getSaving`,
        `/api/moapay/pay/saving/getSaving`,
        { memberId: id },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (response.status == 200) {
        //daily값은 ,를 기준으로 분리해서 넣기
        const dailyData = response.data.data.daily; // 데이터 가져오기
        const separatedData = dailyData.split(",").map(Number); // ','로 문자열 분리 후 숫자 배열로 변환

        // 오늘 날짜 정보
        const today = new Date();
        const currentMonth = today.getMonth(); // 0이 1월을 나타냄
        const currentYear = today.getFullYear();

        // 이번 달의 첫 번째 날 정보
        const firstDayOfMonth = new Date(currentYear, currentMonth, 1);

        // 이번 달 1일의 요일 (0: 일요일, 1: 월요일, ..., 6: 토요일)
        const startDayOfMonth = firstDayOfMonth.getDay();

        // 앞에 추가할 0의 개수는 첫 번째 요일의 인덱스와 같음
        const filledData = Array(startDayOfMonth).fill(0).concat(separatedData);
        console.log(filledData);

        setSavingData({
          memberId: response.data.data.memberId,
          todayAmount: response.data.data.todayAmount,
          limitAmount: response.data.data.limitAmount,
          amount: response.data.data.amount,
          daily: filledData,
        });
      }
    } catch (e) {
      console.log(e);
    }
  };

  // 선택된 주차의 데이터를 필터링하여 계산하는 함수
  const calculateWeekAmount = () => {
    const startIndex = (selectedWeek - 1) * 7; // 주차의 시작 인덱스
    const endIndex = startIndex + 7; // 주차의 끝 인덱스
    const currentWeekData = savingData.daily.slice(startIndex, endIndex); // 해당 주차의 데이터 추출

    setWeekAmountList(currentWeekData); // 주차별 금액 리스트 설정
    const totalAmount = currentWeekData.reduce((acc, curr) => acc + curr, 0); // 주차별 총 금액 계산
    setWeekTotalAmount(totalAmount); // 주차별 총 금액 설정
  };

  useEffect(() => {
    // 주차와 데이터 설정
    setSelectedWeek(getWeekOfMonth());
    getSavingData();
  }, []);

  useEffect(() => {
    if (savingData.daily.length > 0) {
      calculateWeekAmount();
    }
  }, [selectedWeek, savingData]); // 주차 변경이나 데이터 변경 시 금액 계산

  // 왼쪽 화살표 클릭 시
  const handleLeftClick = () => {
    setSelectedWeek((prevWeek) => Math.max(1, prevWeek - 1)); // 1보다 작아지지 않도록 처리
  };

  // 오른쪽 화살표 클릭 시
  const handleRightClick = () => {
    const currentWeek = getWeekOfMonth(); // 현재 주차 계산
    setSelectedWeek((prevWeek) => Math.min(currentWeek, prevWeek + 1)); // 현재 주차보다 커지지 않도록 처리
  };

  return (
    <>
      {savingMode == false ? (
        <PreView>
          {settingStep !== 3 ? (
            <header>
              <div
                onClick={() => {
                  navigate(PATH.HOME);
                }}
              >
                <FontAwesomeIcon icon={faChevronLeft} />
              </div>
            </header>
          ) : null}

          {settingStep === 1 ? (
            <FirstStep>
              <div className="title">
                도전!
                <br />
                이번 달 소비 줄이기
              </div>
              <div className="sub">
                <img src="/assets/image/prinre.png" />
                <p className="name">모아 공주</p>
                <p>
                  {name}님이 돈을 덜 쓰도록, <br />
                  제가 도와드릴게요
                </p>
              </div>
              <div className="bottom">
                <button onClick={() => setSettingStep(2)}>시작하기</button>
              </div>
            </FirstStep>
          ) : settingStep === 2 ? (
            <SecondStep>
              <div className="sub">
                <img src="/assets/image/prinreface.png" />
                <p className="name">모아 공주</p>
                <p>
                  목표금액을 설정해볼까요?
                  {/* <br /> */}
                  {/* 저번 달엔 {calculatedPrice}원 사용했어요. */}
                </p>
              </div>
              <div className="setting-price">
                <p>목표금액</p>
                <div>
                  <input value={goal} onChange={handleInputChange} />
                  <span>만원</span>
                </div>
              </div>
              <div className="bottom">
                <button onClick={settingGoal}>설정하기</button>
              </div>
            </SecondStep>
          ) : settingStep === 3 ? (
            <LastStep>
              <p>
                목표설정 완료 !<br />
                {name}님은 분명 성공할거예요!
              </p>
              <div
                style={{
                  width: "250px",
                  height: "250px",
                  marginTop: "20px",
                  position: "relative",
                }}
              >
                <img
                  style={{ width: "100%", height: "100%" }}
                  src="/assets/gif/coin.gif"
                />
                <div
                  style={{
                    position: "absolute",
                    width: "100%",
                    height: "30px",
                    backgroundColor: "white",
                    bottom: "0px",
                  }}
                ></div>
              </div>
              <div className="bottom">
                <button onClick={() => setSavingMode(true)}>이동하기</button>
              </div>
            </LastStep>
          ) : null}
        </PreView>
      ) : (
        <Wrapper>
          {savingAlram === null && (
            <AlramModal className={savingAlram === false ? "close" : ""}>
              <p>
                {name}님이 돈을 많이 쓰면
                <br />
                알려드릴까요?{" "}
              </p>
              <img src="/assets/image/prinreface.png" />
              <button
                onClick={() => setSavingAlram(true)}
                style={{ backgroundColor: "#DB94EF" }}
              >
                동의하고 알림받기
              </button>
              <button onClick={() => setSavingAlram(false)}>닫기</button>
            </AlramModal>
          )}
          <div className="choice-week">
            <FontAwesomeIcon
              icon={faCaretLeft}
              style={{ fontSize: "18px", cursor: "pointer" }}
              onClick={handleLeftClick} // 왼쪽 화살표 클릭 시
            />
            <p>
              {new Date().getMonth() + 1}월 {getNumberInKorean(selectedWeek!)}째
              주
            </p>
            <FontAwesomeIcon
              icon={faCaretRight}
              style={{ fontSize: "18px", cursor: "pointer" }}
              onClick={handleRightClick} // 오른쪽 화살표 클릭 시
            />
          </div>
          <div className="total">
            <p>한 주 동안</p>
            <p>{weekTotalAmount.toLocaleString()}원 썼어요</p>
          </div>
          <div className="avg">
            <p>하루 평균 결제💸</p>
            <p>
              {Number((weekTotalAmount! / 7).toFixed(0)).toLocaleString()}원
            </p>
          </div>
          <SmallBarGraph consumptionList={weekAmountList} />
        </Wrapper>
      )}
    </>
  );
};

export default Saving;
