import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import SquareBtn from "../SquareBtn/SquareBtn";
import Product from "../Product/Product";
import { PATH } from "./../../../constants/path";
import { useAuthStore } from "../../../store/AuthStore";
import apiClient from "../../../axios";

apiClient.get('/endpoint') // https://your-api-base-url.com/endpoint

import line from "./../../../assets/image/dutch_line.png";
// import { useEffect } from 'react';
import {
  Wrapper,
  Price,
  Title,
  PartiList,
  PartiInfo,
  Btn,
  WarningMessage,
} from "./Participant.styles";

// useEffect(() => {
//   // 참가자 목록 불러오기_참가자가 새로 들어올 때마다 리스트 조회가 이루어져야함?
// }, [])
interface ParticipantInfo {
  index: number;
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number | null;
  status: string;
}

interface ParticipantProps {
  maxNum?: number | null;
  roomId: string;
  setDutchParticipants: (
    participants: {
      index: number;
      uuid: string;
      memberId: string;
      memberName: string;
      amount: number | null;
      status: string;
    }[]
  ) => void;
  participants: {
    index: number;
    uuid: string;
    memberId: string;
    memberName: string;
    amount: number | null; // 초기값은 null일 수 있도록 설정
    status: string;
  }[];
  leaveRoom: () => void;
  confirm: () => void;
  setProcess?: (step: number) => void;
  process: number;
  roomInfo?: DutchPayInfo;
  setConfirmAmount: (amount: number) => void;
  totalPrice: number;
  isHostProp: boolean;
  merchantName: string;
  merchantThumbnailUrl: string;
  loading: boolean;
}

type UUID = string; // UUID를 문자열로 간주

// DutchPayList 아이템 인터페이스
interface DutchPayItem {
  uuid: UUID;
  memberId: UUID;
  memberName: string;
  amount: number; // 또는 number, 필요에 따라 선택
  status: string;
}

// 메인 DutchPayInfo 인터페이스
interface DutchPayInfo {
  dutchUuid: UUID;
  memberCnt: number; // 또는 number, 필요에 따라 선택
  orderId: UUID;
  merchantId: UUID;
  merchantName: string;
  categoryId: string;
  totalPrice: number; // 또는 number, 필요에 따라 선택
  dutchPayList: DutchPayItem[]; // DutchPayItem 배열
}

interface ParticipantInfo {
  index: number;
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number | null;
  status: string;
}

// interface Participant {
//   name: string;
//   id: number;
//   amount: string;
// }

const Participant = ({
  maxNum = null,
  roomId,
  setDutchParticipants,
  participants,
  leaveRoom,
  confirm,
  setProcess,
  process,
  roomInfo,
  totalPrice,
  setConfirmAmount,
  isHostProp,
  merchantName,
  merchantThumbnailUrl,
  loading,
}: ParticipantProps) => {
  const convertDutchPayItemsToParticipantInfo = (
    items: DutchPayItem[]
  ): ParticipantInfo[] => {
    return items.map((item, index) => ({
      index, // 현재 인덱스 값 추가
      uuid: item.uuid.toString(), // UUID를 문자열로 변환
      memberId: item.memberId.toString(), // UUID를 문자열로 변환
      memberName: item.memberName,
      amount: item.amount, // amount는 null일 수 없다고 가정
      status: item.status,
    }));
  };

  console.log("totalPrice : ", totalPrice);
  const [confirmPrice, setConfirmPrice] = useState<number>();

  useEffect(() => {
    if (roomInfo && roomInfo.dutchPayList) {
      console.log("방 정보 : ", roomInfo);

      // roomInfo.dutchPayList를 ParticipantInfo로 변환
      const participantInfoList = convertDutchPayItemsToParticipantInfo(
        roomInfo.dutchPayList
      );

      for (let i = 0; i < roomInfo.dutchPayList.length; i++) {
        if (id == null) {
          setConfirmAmount(100000);
        }

        if (roomInfo.dutchPayList[i].memberId === id) {
          setConfirmAmount(roomInfo.dutchPayList[i].amount ?? 0);
        }
      }

      console.log("==============", roomInfo.totalPrice);
      setConfirmPrice(roomInfo.totalPrice);

      // 변환된 리스트를 setDutchParticipants로 설정
      setDutchParticipants(participantInfoList);

      // roomInfo가 null이 아닐 때 실행할 추가 작업
    } else {
      console.warn("roomInfo is null");
    }
  }, [roomInfo]);

  const nav = useNavigate();
  const { name, id } = useAuthStore();

  // const [participants, setParticipants] = useState([])
  const [isHost, setIsHost] = useState<boolean>(false);
  const [dutchStart, setDutchStart] = useState<boolean>(false);
  const [showWarning, setShowWarning] = useState<boolean>(false); // 경고 메시지 상태 추가
  const [price, setPrice] = useState<number>(totalPrice || 0); // 더치페이 상품 가격

  useEffect(() => {
    // totalPrice는 숫자형으로 변환하되, 유효하지 않을 경우 0으로 설정
    const storedTotalPrice = parseInt(
      localStorage.getItem("totalPrice") || "0",
      10
    );
    const validatedTotalPrice = isNaN(storedTotalPrice) ? 0 : storedTotalPrice;

    setPrice(validatedTotalPrice);
    // setPrice(15000); // 테스트용 추후 지울 예정

    // 값 로그로 출력
    console.log("Total Price:", localStorage.getItem("totalPrice"));
  }, []);

  // [미완]특정 참가자 추방(삭제) api 호출 함수_id로 삭제하게 될 것 같아 id를 인자로 넘겨놓음(나중에 다시 체크하기)
  const onDelete = async (id: number) => {
    // console.log(id, "삭제")
    try {
      const response = await apiClient.delete(`요청 api 주소 입력`, {
        headers: {
          "Content-Type": "application/json",
        },
        // withCredentials: true, // 쿠키를 포함하여 요청 <--- 필요한가?
      });
      if (response.status === 200) {
        console.log("삭제 완료");
      }
    } catch (err) {
      console.error("에러 발생", err);
    }
  };

  // 더치페이 시작 버튼을 눌렀을 시
  const onClickDutchStart = () => {
    // if (주최자일 경우에만) {
    console.log("더치페이 시작 버튼 클릭");
    // setParticipantsPrice(Math.floor(price / participants.length)) // 전체금을 참여 인원수로 나눈값(나머지 버림)을 자동 배분 결제금으로 저장
    const participantPrice = Math.floor(price / participants.length);
    console.log("자동분배금 확인용", Math.floor(price / participants.length));
    setDutchStart(true);
    // }

    // 금액 자동 분배하기
    participants.map((participant, index) => {
      if (index === 0) {
        participant.amount =
          price - participantPrice * (participants.length - 1);
      } else {
        participant.amount = participantPrice;
      }
    });
  };

  // const getDutchAmount = async () => {
  //   const requestBody = {
  //     roomId: roomId || null,
  //     memberId: id || null,
  //   };

  //   console.log(requestBody);

  //   try {
  //     const response = await axios.post(
  //       "http://localhost:18020/moapay/core/dutchpay/getMyPrice",
  //       requestBody
  //     );

  //     console.log("getDutchAmount 응답 결과:", response.data.data.price);
  //     setConfirmAmount(response.data.data.price);
  //   } catch (err) {
  //     console.error("결제금 조회 에러 발생:", err);
  //   }
  // };

  // 결제 요청 버튼을 눌렀을 시
  const onClickRequest = async () => {
    let sumValue: number = 0;
    // 결제 요청시 모든 참가자의 결제금 합이 총 금액(price)과 같은지 확인
    participants.map((participant) => {
      if (participant.amount) {
        sumValue += participant.amount;
      }
    });

    if (sumValue !== price) {
      setShowWarning(true); // 경고 메시지 표시
      setTimeout(() => setShowWarning(false), 1500); // 3초 후 경고 메시지 숨김
    } else {
      // 모든 amount가 입력된 경우 결제 요청 로직 수행
      console.log("결제 요청");
      if (setProcess) {
        setProcess(2);
      }
      await confirm();

      console.log("컨펌 후 확인용", participants);

      // 예시: participants 배열과 id 값이 주어졌다고 가정
      for (let i = 0; i < participants.length; i++) {
        if (participants[i].memberId === id) {
          // amount가 null이 아닐 때만 confirmAmount 설정

          localStorage.setItem("dutchPayId", participants[i].uuid.toString());

          if (participants[i].amount !== null) {
            setConfirmAmount(participants[i].amount ?? -1);
          }
        }
      }

      // getDutchAmount(); // 주최자 결제금 조회
    }

    // setConfirmAmount((participants[0].amount)) // 주최자 결제금 저장

    // leaveRoom()
    // nav(`/dutchpay/invite/${maxNum}/${roomId}`) // 넘어갈 때 local storage에서 maxMember 불러오고 roomid도 넘겨줘야 함

    // // 결제 요청 시 모든 참가자의 amount가 입력되었는지 확인
    // const isAnyamountEmpty = participants.some(participant => participant.amount === '');

    // if (isAnyamountEmpty) {
    //   setShowWarning(true);  // 경고 메시지 표시
    //   setTimeout(() => setShowWarning(false), 1500);  // 3초 후 경고 메시지 숨김
    // } else {
    //   // 모든 amount가 입력된 경우 결제 요청 로직 수행
    //   console.log("결제 요청");
    // }
  };

  const onPaymentStart = () => {
    // getDutchAmount(); // 참가자 결제금 조회

    if (setProcess) {
      setProcess(2); // 참가자 결제 페이지로 이동
    }
    // leaveRoom() // stomp 종료
  };

  const changeAmount = (index: number, value: number) => {
    const updateParticipants = [...participants];
    updateParticipants[index] = { ...updateParticipants[index], amount: value };
    setDutchParticipants(updateParticipants);
  };

  useEffect(() => {
    setDutchStart(false);
    setIsHost(isHostProp);
    // setIsHost(JSON.parse(localStorage.getItem("isHost") || "false")); // localStorage에서 가져오는 코드

    console.log(roomInfo);
  }, []);

  return (
    <Wrapper>
      {/* 더치 페이 시작 버튼을 눌렀을 시에만 보이는 정보들 설정*/}
      {/* api 호출 해서 넘어왔으면 하는 정보: 상품 사진, 상품명, url, 가격*/}
      {dutchStart ? (
        <>
          {/* 더치페이 하여 구매할 상품 정보 */}
          {loading? <p>주문 정보를 불러오는 중...</p> : (
            <Product
              productName={merchantName || "BESPOKE 냉장 4도어 키친핏 615L (UV탈취)"}
              productUrl={
                "https://www.ssg.com/item/itemView.ssg?itemId=1000566517100"
              }
              productImg={merchantThumbnailUrl || "/assets/image/ref1.png"}
            />
          )}

          {/* <div>총 금액: {prduct_price}원</div> */}
          <Price>총 금액: {price.toLocaleString()} 원</Price>

          {/* 구분 점선 */}
          <img src={line} />
        </>
      ) : null}

      {/* 참여자의 분자값: 초대자가 입력한 인원 수가 (props로 넘어오게 해야함) */}
      <Title>
        참여자({participants.length}/{maxNum})
      </Title>
      {/* 참가자가 있을 경우에만 출력되도록 */}
      <PartiList>
        {participants.length > 0
          ? participants.map((participant, index) => (
              <PartiInfo key={index}>
                {/* 랜덤 프로필_랜덤 사진 */}
                <div
                  style={{
                    border: "2px solid black",
                    width: "50px",
                    height: "50px",
                    borderRadius: "100%",
                  }}
                ></div>

                <div>{participant.memberName}</div>
                {/* <div>삭제 아이콘</div> */}
                {!dutchStart && participant.index > 0 && isHost && (
                  <svg
                    onClick={() => onDelete(participant.index)}
                    xmlns="http://www.w3.org/2000/svg"
                    x="0px"
                    y="0px"
                    width="20"
                    height="20"
                    viewBox="0 0 48 48"
                  >
                    <path d="M 38.982422 6.9707031 A 2.0002 2.0002 0 0 0 37.585938 7.5859375 L 24 21.171875 L 10.414062 7.5859375 A 2.0002 2.0002 0 0 0 8.9785156 6.9804688 A 2.0002 2.0002 0 0 0 7.5859375 10.414062 L 21.171875 24 L 7.5859375 37.585938 A 2.0002 2.0002 0 1 0 10.414062 40.414062 L 24 26.828125 L 37.585938 40.414062 A 2.0002 2.0002 0 1 0 40.414062 37.585938 L 26.828125 24 L 40.414062 10.414062 A 2.0002 2.0002 0 0 0 38.982422 6.9707031 z"></path>
                  </svg>
                )}
                {
                  // !isHost&&
                  (participant.index === 0 || !isHost) && !dutchStart && (
                    <div></div>
                  )
                }

                {/* 해당 사용자가 지불해야 할 금액 */}
                {/* 자동으로 n등분 해서 분배해줘야 함_안 나눠 떨어질 경우: 주최자를 제외한 모두에게 (전체 값//사람 수)값 적용. 주최자는 (전체 값-(참가자)*(n-1)) */}
                {dutchStart && isHost && (
                  <input
                    value={Number(participant.amount)}
                    onChange={(e) => {
                      changeAmount(index, Number(e.target.value));
                    }}
                    type="number"
                    min="0"
                  />
                )}
                {/* <div>뭐징..</div> */}
                {process === 1 && !isHost && (
                  <input
                    disabled
                    value={Number(participant.amount)}
                    onChange={(e) => {
                      changeAmount(index, Number(e.target.value));
                    }}
                    type="number"
                    min="0"
                  />
                )}
              </PartiInfo>
            ))
          : true}
      </PartiList>

      <Btn>
        {dutchStart ? (
          <SquareBtn
            text={"결제 요청하기"}
            color={"rgba(255, 255, 255, 0.7)"}
            onClick={onClickRequest}
          />
        ) : participants.length > 0 && isHost ? (
          <SquareBtn
            text={"더치페이 시작"}
            color="rgba(135, 72, 243, 0.74)"
            onClick={onClickDutchStart}
          />
        ) : // null
        process === 1 && !isHost ? (
          <SquareBtn
            text={`결제하기`}
            color="rgba(135, 72, 243, 0.74)"
            onClick={onPaymentStart}
          />
        ) : null}
        {/* 경고 메시지 출력 */}
      </Btn>
      {showWarning && (
        <WarningMessage>결제 금액을 다시 확인해주세요.</WarningMessage>
      )}
    </Wrapper>
  );
};

export default Participant;
