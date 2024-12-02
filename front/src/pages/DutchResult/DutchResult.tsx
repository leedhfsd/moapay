import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PATH } from "../../constants/path";
import { useAuthStore } from "../../store/AuthStore";
import apiClient from "../../axios";
import { AxiosError } from "axios";
import { useParams } from "react-router-dom";
import triangle from "./../../assets/image/triangle.png";
import DutchPayImg from "../../assets/image/DutchPay.gif";
import Comfirm from "../../assets/image/confirm.webp";

import Product from "../../components/dutch/Product/Product";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faRotateLeft,
  faCircleCheck,
  faCircle,
} from "@fortawesome/free-solid-svg-icons";

import {
  Container,
  Header,
  Button,
  MemberList,
  MemberItem,
  MemberStatus,
  Wrapper,
  Main,
  Reload,
  BackImg,
  Merchant,
} from "./DutchResult.styles"; // 스타일을 임포트
import SquareBtn from "../../components/dutch/SquareBtn/SquareBtn";

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
  roomid: string; // roomId를 props로 받음
}

interface OrderInfo {
  status: string;
  message: string;
  data: {
    thumbnailUrl: string;
    itemNames: string[];
    url: string | null;
  };
}

const DutchResult = () => {
  const { roomId } = useParams(); // URL에서 roomId 가져오기
  const navigate = useNavigate();
  const [roomInfo, setRoomInfo] = useState<DutchCompliteResponse | null>(null); // 방 정보를 저장하는 state
  const [orderInfo, setOrderInfo] = useState<OrderInfo | null>(null); // orderInfo 데이터를 저장하는 state
  const { accessToken } = useAuthStore();
  const [orderId, setOrderId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true); // 로딩 상태 관리

  // 완료된 멤버 수 계산
  const completedMembers = roomInfo
    ? roomInfo.data.dutchPayList.filter((member) => member.status === "DONE")
        .length
    : 0;
  const totalMembers = roomInfo ? roomInfo.data.dutchPayList.length : 0;

  // 컴포넌트가 처음 마운트될 때 실행
  useEffect(() => {
    getRoomInfo();
  }, []); // roomId가 변경될 때마다 실행

  useEffect(() => {
    if (roomInfo?.data.orderId) {
      setOrderId(roomInfo.data.orderId); // roomInfo에서 orderId를 설정
      getOrderInfo(roomInfo.data.orderId); // orderInfo를 불러옴
    }
  }, [roomInfo]);

  const getOrderInfo = async (orderId: string) => {
    console.log("order Info : ", orderId);
    try {
      const response = await apiClient.get(
        `api/moapay/core/dutchpay/orderInfo/${orderId}`,
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`, // Authorization 헤더에 Bearer 토큰 추가
          },
        }
      );
      if (response?.status === 200) {
        const orderData = response.data;
        console.log(orderData);
        setOrderInfo(orderData);
      }
    } catch (e) {
      const error = e as AxiosError;
      console.log(error);
    } finally {
      setLoading(false); // 로딩 완료
    }
  };

  const getRoomInfo = async () => {
    console.log(roomId);
    try {
      const response = await apiClient.get(
        `api/moapay/core/dutchpay/getDutchRoomInfo/${roomId}`,
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
    <Wrapper>
      <Header>
        <img
          src={
            completedMembers === totalMembers && totalMembers > 0
              ? Comfirm
              : DutchPayImg
          }
          alt=""
          style={{ width: "120px" }} // 완료된 이미지의 URL로 바꿉니다.
        />
        <br />
        {completedMembers === totalMembers && totalMembers > 0
          ? "더치페이 완료"
          : "더치페이 진행 중"}
        <Reload>
          <Button onClick={handleButtonClick}>
            <FontAwesomeIcon icon={faRotateLeft} />
          </Button>
        </Reload>
      </Header>

      {loading ? ( // 로딩 상태에 따라 다른 UI 표시
        <p>주문 정보를 불러오는 중...</p>
      ) : roomInfo ? (
        <div>
          <Main>
            {/* OrderInfo 데이터 렌더링 */}
            {orderInfo ? (
              <div>
                <Product
                  productName={
                    Array.isArray(orderInfo.data.itemNames) &&
                    orderInfo.data.itemNames.length > 0
                      ? orderInfo.data.itemNames[0]
                      : "BESPOKE 냉장 4도어 키친핏 615L (UV탈취)"
                  }
                  productImg={
                    orderInfo.data.thumbnailUrl || "/assets/image/ref2.png"
                  }
                  productUrl={orderInfo.data.url || "#"} // URL도 null 체크를 추가
                />
              </div>
            ) : (
              <p>주문 정보가 없습니다.</p> // 주문 정보가 없을 경우 메시지
            )}
            <Merchant>
              <div
                style={{
                  fontSize: "30px",
                  fontWeight: "bold",
                  margin: "10px",
                }}
              >
                ({completedMembers} / {totalMembers})
              </div>
              {/* 총 금액 표시 */}
              <div style={{ fontSize: "25px" }}>
                {roomInfo.data.totalPrice.toLocaleString()} 원
              </div>
            </Merchant>

            <MemberList>
              {roomInfo.data.dutchPayList.map((member) => (
                <MemberItem key={member.uuid}>
                  {member.status === "DONE" ? (
                    <MemberStatus>
                      <FontAwesomeIcon
                        icon={faCircleCheck}
                        style={{ color: "#939cff", fontSize: "25px" }}
                      />
                    </MemberStatus>
                  ) : (
                    <MemberStatus>
                      <FontAwesomeIcon
                        icon={faCircle}
                        style={{ color: "#e4e6ff", fontSize: "25px" }}
                      />
                    </MemberStatus>
                  )}
                  {member.memberName} - {member.amount ?? "No Amount"}원
                </MemberItem>
              ))}
            </MemberList>
            {completedMembers === totalMembers && totalMembers > 0 ? (
              <SquareBtn
                text={`홈으로 이동`}
                color="rgba(255, 255, 255)"
                onClick={() => {
                  navigate(PATH.HOME);
                }}
              />
            ) : (
              <></>
            )}
          </Main>
        </div>
      ) : (
        <p>방 정보를 불러오는 중...</p>
      )}

      <BackImg>
        <img src={triangle} />
        <img src={triangle} />
        <img src={triangle} />
      </BackImg>
    </Wrapper>
  );
};

export default DutchResult;
