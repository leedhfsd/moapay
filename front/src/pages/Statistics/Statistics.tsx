import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { TopWrapper, Wrapper } from "./Statistics.styles";
import Wave from "../../components/statistics/Wave/Wave";
import Nav from "../../components/statistics/Nav/Nav";
import DonutChart from "../../components/statistics/Chart/DonutChart/DonutChart";
import StatisticDonutChartText from "../../components/statistics/Text/StatisticDonutChartText/StatisticDonutChartText";
import {
  faChevronRight,
  // faCaretDown,
  // faCaretUp,
  faChevronLeft,
} from "@fortawesome/free-solid-svg-icons";
import {
  Top,
  Month,
  Info,
  Bottom,
  // DropDownIcon,
  NowDate,
  ImageBox,
  TextBox,
} from "./Statistics.styles";
import { PATH } from "../../constants/path";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import { categoryData } from "../../store/CardStore";
import { useAuthStore } from "../../store/AuthStore";
import apiClient from "../../axios";
const Statistics = () => {
  const { id, accessToken, name } = useAuthStore();

  const [gender, setGender] = useState<string>("");
  const navigator = useNavigate();
  const location = useLocation();
  const paths = [
    `${PATH.STATISTICS}${PATH.STATISTICS_CONSUMPTION}`,
    `${PATH.STATISTICS}${PATH.STATISTICS_BENEFITS}`,
    `${PATH.STATISTICS}${PATH.STATISTICS_ANALYSIS}`,
    `${PATH.STATISTICS}${PATH.STATISTICS_SAVING}`,
  ];
  //이번 달을 첫 데이터값으로 지정
  const [selectedYear, setSelectedYear] = useState<number>(
    new Date().getFullYear()
  );
  const [selectedMonth, setSelectedMonth] = useState<number>(
    new Date().getMonth() + 1
  );
  const [mode, setMode] = useState<string>("Donut");
  const [navPosition, setNavPosition] = useState<string>(
    `calc(calc(100% / 4) * 0)`
  );
  const [calculatedPrice, setCalculatedPrice] = useState<number>(0);

  // 카테고리 데이터
  const [dataList, setDataList] = useState<categoryData[] | null>([]);
  const [comparisonAmount, setComparisonAmount] = useState<number>(0);

  //절약
  const [savingGoal, setSavingGoal] = useState<number>(0);
  const [savingUse, setSavingUse] = useState<number>(0);
  const [daysLeft, setDaysLeft] = useState<number>(0);

  const handlePrevMonth = () => {
    console.log("날짜변경 - 줄어듬");
    if (selectedMonth === 1) {
      setSelectedYear((prevYear) => prevYear - 1);
      setSelectedMonth(12);
    } else {
      setSelectedMonth((prevMonth) => prevMonth - 1);
    }
  };

  const handleNextMonth = () => {
    console.log("날짜변경 - 늘어남");
    if (
      selectedYear == new Date().getFullYear() &&
      selectedMonth > new Date().getMonth()
    )
      return;
    if (selectedMonth === 12) {
      setSelectedYear((prevYear) => prevYear + 1);
      setSelectedMonth(1);
    } else {
      setSelectedMonth((prevMonth) => prevMonth + 1);
    }
  };

  /**
   * 특정 달에 대한 소비 데이터 가져오기 - 년도와 월을 보내야함(selectedYear selectedMonth)
   */
  const getConsumptionData = async () => {
    console.log("getConsumtionData 함수 실행");
    console.log(selectedYear, selectedMonth);
    console.log("=======================================");
    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/statistics/consumption/${selectedYear}/${selectedMonth}`,
        `/api/moapay/pay/statistics/consumption/${selectedYear}/${selectedMonth}`,
        {
          memberId: id,
        },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      setDataList(response.data.data.paymentStatistics);
      //받은 소비량 더하기
      setCalculatedPrice(response.data.data.totalAmounts);
    } catch (e) {
      console.log(e);
    }
  };

  /**
   * 특정 달에 대한 혜택 데이터 가져오기 - 년도와 월을 보내야함
   */
  const getBenefitData = async () => {
    console.log("getBenefitData 함수 실행");
    console.log(selectedYear, selectedMonth);
    console.log("=======================================");
    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/statistics/benefit/${selectedYear}/${selectedMonth}`,
        `/api/moapay/pay/statistics/benefit/${selectedYear}/${selectedMonth}`,
        {
          memberId: id,
        },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      setDataList(response.data.data.paymentStatistics);
      const totalPrice = response.data.data.totalBenefits;
      setCalculatedPrice(totalPrice);
    } catch (e) {
      console.log(e);
    }
  };

  /**
   * nav의 값에 따라 컴포넌트 변경
   *   const [savingGoal, setSavingGoal] = useState<number>(0);
  const [savingUse, setSavingUse] = useState<number>(0);
   */
  const changeComponent = async (index: number) => {
    console.log("nav index ", index);
    setNavPosition(`calc(calc(100% / 4) * ${index})`);
    //데이터 요청받아서 navigate할때 같이 보내줘야함 -> navigator(paths[index],{state:[datalist]]})
    //받을 때는 locationt사용   const location = useLocation();  const data = location.state;
    if (index == 0) {
      setMode("Donut");
      try {
        await getConsumptionData();
        console.log("소비 데이터 가져옴 ", dataList);
        navigator(paths[index], { state: dataList });
      } catch (e) {
        console.log(e);
      }
    } else if (index == 1) {
      //데이터 요청받아서 navigate할때 같이 보내줘야함 -> navigator(paths[index],{state:[datalist]]})
      //받을 때는 locationt사용   const location = useLocation();  const data = location.state;
      setMode("Donut");
      try {
        await getBenefitData();
        console.log("혜택 데이터 가져옴 ", dataList);
        navigator(paths[index], { state: dataList });
      } catch (e) {
        console.log(e);
      }
    } else if (index == 2) {
      //나의 해당 월 사용량 가져오기
      setSelectedYear(
        new Date().getMonth() + 1 === 1
          ? new Date().getFullYear() - 1
          : new Date().getFullYear()
      );
      //저번달 소비로 가져오기
      setSelectedMonth(
        new Date().getMonth() + 1 === 1 ? 12 : new Date().getMonth()
      );
      getConsumptionData();
      //또래 비교금액 가져오기
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/analysis/getAverage`,
        `/api/moapay/pay/analysis/getAverage`,
        { memberId: id },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      console.log(
        "비교 금액 구하기",
        calculatedPrice - response.data.data.average
      );
      setComparisonAmount(calculatedPrice - response.data.data.average);
      setGender(response.data.data.gender == "F" ? "여" : "남");
      setMode("BarGraph");
      navigator(paths[index]);
    } else {
      //저번달 소비로 가져오기
      setMode("Ano");
      setSelectedYear(
        new Date().getMonth() + 1 === 1
          ? new Date().getFullYear() - 1
          : new Date().getMonth() + 1
      );
      //저번달 소비로 가져오기
      setSelectedMonth(
        new Date().getMonth() + 1 === 1 ? 12 : new Date().getMonth() - 1
      );
      getConsumptionData();

      //saving-storage에서 saving-mode가 true일때
      if (localStorage.getItem("saving-storage")) {
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
          setSavingGoal(response.data.data.limitAmount);
          setSavingUse(response.data.data.amount);
          const today = new Date();

          // 이번 달의 마지막 날 계산 (다음 달의 1일에서 하루를 빼면 이번 달 마지막 날)
          const lastDayOfMonth = new Date(
            today.getFullYear(),
            today.getMonth() + 1,
            0
          );

          // 남은 일수 계산
          const remainingDays = lastDayOfMonth.getDate() - today.getDate();

          // 상태 업데이트
          setDaysLeft(remainingDays);
        } catch (e) {
          console.log(e);
        }
      }
      //저번달 사용량을 함께 보냄
      navigator(paths[index], { state: { calculatedPrice } });
    }
  };

  useEffect(() => {
    const currentPath = location.pathname;
    // URL 경로에 맞게 index 값을 설정
    const index = paths.findIndex((path) => path === currentPath);
    console.log("useEffect index: " + index);
    if (index !== -1) {
      changeComponent(index); // URL에 맞는 컴포넌트를 렌더링
    }
  }, []);

  useEffect(() => {
    if (window.location.pathname === paths[0]) {
      getConsumptionData();
    } else if (window.location.pathname === paths[1]) {
      getBenefitData();
    }
  }, [selectedMonth, selectedYear]);

  useEffect(() => {
    if (window.location.pathname === paths[0]) {
      if (dataList) {
        navigator(paths[0], { state: dataList });
      }
    } else if (window.location.pathname === paths[1]) {
      if (dataList) {
        navigator(paths[1], { state: dataList });
      }
    }
  }, [dataList]);

  return (
    <>
      <Wrapper className="Wrapper">
        <Wave />
        <Top className="Top">
          {mode === "Donut" && (
            <>
              <Month>
                <div className="month">
                  <div onClick={handlePrevMonth}>
                    <FontAwesomeIcon icon={faChevronLeft} />
                  </div>
                  <p>{`${selectedYear}.${String(selectedMonth).padStart(
                    2,
                    "0"
                  )}`}</p>
                  <div onClick={handleNextMonth}>
                    <FontAwesomeIcon icon={faChevronRight} />
                  </div>
                </div>
              </Month>
              <Info>
                <DonutChart dataList={dataList} />
                <StatisticDonutChartText
                  text={`${selectedMonth}월에는\n${calculatedPrice}원\n${
                    location.pathname.includes("consumption")
                      ? "소비했어요!"
                      : location.pathname.includes("benefits")
                      ? "혜택을 받았어요!"
                      : ""
                  }`}
                />
              </Info>
            </>
          )}
          {mode === "BarGraph" && (
            <TopWrapper>
              <NowDate>{`2024년 9월엔...`}</NowDate>
              <ImageBox>
                <img
                  src={
                    comparisonAmount < 0
                      ? "/assets/image/good-pig.png"
                      : "/assets/image/sad-pig.png"
                  }
                ></img>
              </ImageBox>
              <TextBox>
                {`${name}님은 또래 ${gender}성에 비해\n${Math.abs(
                  comparisonAmount
                ).toLocaleString()}원 ${
                  comparisonAmount > 0 ? "더 사용했어요" : "덜 사용했어요"
                }`
                  .split("\n")
                  .map((line, index) => (
                    <span key={index}>
                      {line}
                      <br />
                    </span>
                  ))}
              </TextBox>
            </TopWrapper>
          )}
          {mode === "Ano" && (
            <div className="saving">
              <p className="title">
                {new Date().getMonth() + 1}월 목표 중 남은 금액
              </p>
              <div className="price">
                <p>{savingUse.toLocaleString()}</p>
                <p>/</p>
                <p>{savingGoal.toLocaleString()}원</p>
              </div>
              <div className="sub">
                <div>
                  <img src="/assets/image/prinrefacezoom.png" />
                  {Math.round((savingUse / savingGoal) * 100) > 100 ? (
                    <>
                      <p>
                        목표 금액보다 더 사용했어요
                        <br />
                        불필요한 지출을 줄여보는게 어떨까요?
                      </p>
                    </>
                  ) : (
                    <>
                      <p>
                        목표 금액의{" "}
                        <span style={{ color: "red " }}>
                          {Math.round((savingUse / savingGoal) * 100)}%
                        </span>
                        를 썼어요.
                        <br />
                        앞으로 하루{" "}
                        <span style={{ color: "#4258ff " }}>
                          {Math.round(
                            (savingGoal - savingUse) /
                              (daysLeft === 0 ? 1 : daysLeft)
                          )}
                          원
                        </span>
                        만 쓰면 돼요.
                      </p>
                    </>
                  )}
                </div>
              </div>
            </div>
          )}
        </Top>
        <Bottom className="Bottom">
          <Nav navPosition={navPosition} changeComponent={changeComponent} />
          <Outlet />
        </Bottom>
      </Wrapper>
    </>
  );
};
export default Statistics;
