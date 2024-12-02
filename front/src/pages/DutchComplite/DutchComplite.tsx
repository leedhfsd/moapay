import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PATH } from "../../constants/path";
import { useAuthStore } from "../../store/AuthStore";
import apiClient from "../../axios";
import { AxiosError } from "axios";

interface DutchPayMember {
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number | null;
  status: "DONE" | "READY" | "PROGRESS" | "CANCEL";
}

interface DutchRoomInfo {
  dutchUuid: string;
  memberCnt: number;
  statusRoom: string;
  orderId: string;
  merchantId: string;
  merchantName: string;
  categoryId: string;
  totalPrice: number;
  dutchPayList: DutchPayMember[];
}

interface DutchCompliteResponse {
  status: string;
  message: string;
  data: DutchRoomInfo;
}

interface DutchCompliteProps {
  roomId: string; // roomId를 props로 받음
}

const DutchComplite = ({ roomId }: DutchCompliteProps) => {
  const navigate = useNavigate();
  const [roomInfo, setRoomInfo] = useState<DutchCompliteResponse | null>(null); // 방 정보를 저장하는 state
  const { accessToken, mode, setPaymentType } = useAuthStore();

  // 컴포넌트가 처음 마운트될 때 실행
  useEffect(() => {
    getRoomInfo();
  }, []); // roomId가 변경될 때마다 실행

  const getRoomInfo = async () => {
    try {
      const response = await apiClient.get(
        `/api/moapay/core/dutchpay/getDutchRoomInfo/${roomId}`,
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`, // Authorization 헤더에 Bearer 토큰 추가
          },
        }
      );
      if (response?.status === 200) {
        const roomData = response.data;
        setRoomInfo(roomData);
      }
    } catch (e) {
      const error = e as AxiosError;
      console.log(error);
    }
  };

  // 버튼 클릭 시 실행될 함수
  const handleButtonClick = () => {
    getRoomInfo();
  };

  return (
    <div>
      {/* 방 정보를 JSON으로 그대로 출력 */}
      {/* 버튼 클릭 시 getRoomInfo 실행 */}
      <button onClick={handleButtonClick}>Get Room Info Again</button>
      {roomInfo ? (
        <div>
          <h2>Room Info</h2>
          <pre>{JSON.stringify(roomInfo, null, 2)}</pre>

          <h3>Members</h3>
          <ul>
            {roomInfo.data.dutchPayList.map((member) => (
              <li key={member.uuid}>
                {member.memberName} - {member.amount ?? "No Amount"}
                {member.status === "DONE" && <h1>완료</h1>}
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p>Loading room info...</p>
      )}
    </div>
  );
};

export default DutchComplite;
