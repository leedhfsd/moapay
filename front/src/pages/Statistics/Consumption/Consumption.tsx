import { useLocation } from "react-router-dom";
import List from "../../../components/statistics/List/List";
import { useEffect, useState } from "react";
import { categoryData } from "../../../store/CardStore";
import { useAuthStore } from "../../../store/AuthStore";
import axios from "axios";
import apiClient from "../../../axios";

const Consumption = () => {
  const { id, accessToken } = useAuthStore();
  const location = useLocation();
  const [consumptionList, setConsumptionList] = useState<categoryData[]>(
    location.state || [] // location.state가 없을 때 빈 배열로 초기화
  );
  console.log("소비 location은 있을까용 >? ", location.state);

  const getConsumptionData = async () => {
    console.log("first get Data");
    try {
      const response = await apiClient.post(
        `/api/moapay/pay/statistics/consumption/${new Date().getFullYear()}/${
          new Date().getMonth() + 1
        }`,
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
      setConsumptionList(response.data.data.paymentStatistics);
    } catch (e) {
      console.log(e);
    }
  };

  // 처음 렌더링될 때 데이터를 가져오고, location.state가 변경될 때 consumptionList를 업데이트
  useEffect(() => {
    if (location.state) {
      // location.state가 있을 때는 해당 데이터를 사용
      console.log("location.state를 기반으로 리스트 업데이트");
      setConsumptionList(location.state);
    } else {
      console.log("데이터 가져올 거에옹");
      getConsumptionData(); // location.state가 없을 때는 API 호출
    }
  }, [location.state]); // location.state가 변경될 때 호출

  return (
    <>
      <List consumptionList={consumptionList} />
    </>
  );
};

export default Consumption;
