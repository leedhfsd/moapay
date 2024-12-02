import { useEffect, useState } from "react";
import { Graph, Wrapper } from "./Analysis.styles";
import MixedChart from "../../../components/statistics/Chart/BarGraph/BarGraph";
import Toggle from "../../../../src/components/statistics/Toggle/Toggle";
import axios from "axios";
import { useAuthStore } from "../../../store/AuthStore";
import apiClient from "../../../axios";

const Analysis = () => {
  const { id, accessToken } = useAuthStore();
  const [consumptionMode, setConsumptionMode] = useState<boolean>(true);
  /**
   * 데이터 타입 : 한달 소비량 / 혜택량
   * [50000,30000,...]
   */
  const [userDataList, setUserDataList] = useState<number[]>([]);
  const [avgDataList, setAvgDataList] = useState<number[]>([]);

  useEffect(() => {
    if (consumptionMode) {
      //매달 소비 총액 가져오기
      getMonthlyConsumptionList();
    } else {
      //매달 혜택 총액 가져오기
      getMonthlyBenefitList();
    }
  }, [consumptionMode]);
  //매달 소비 총액 가져오기
  const getMonthlyConsumptionList = async () => {
    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/statistics/consumption`,
        `/api/moapay/pay/statistics/consumption/statistics`,
        { memberId: id },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      const AmountList = response.data.data.monthlyAmounts
        .reverse()
        .map((item: { amount: number }) => item.amount);

      setUserDataList(AmountList);
    } catch (e) {
      console.log(e);
    }
  };

  //매달 혜택 총액 가져오기
  const getMonthlyBenefitList = async () => {
    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/pay/statistics/benefit`,
        `/api/moapay/pay/statistics/benefit/statistics`,
        { memberId: id },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      const benefitList = response.data.data.monthlyBenefits
        .reverse()
        .map((item: { benefit: number }) => item.benefit);

      setUserDataList(benefitList);
    } catch (e) {
      console.log(e);
    }
  };
  const handleToggle = () => {
    setConsumptionMode((current) => !current);
  };
  return (
    <Wrapper>
      <Graph>
        <MixedChart
          consumptionMode={consumptionMode}
          userDataList={userDataList}
          avgDataList={avgDataList}
        />
      </Graph>
      <Toggle consumptionMode={consumptionMode} handleToggle={handleToggle} />
    </Wrapper>
  );
};
export default Analysis;
