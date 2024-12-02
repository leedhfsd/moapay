import triangle from "./../../assets/image/triangle.png";
import Participant from "../../components/dutch/Participant/Participant";
import Modal from "../../components/dutch/Modal/Modal";
import Payment from "../../components/dutch/Payment/Payment";
import SquareBtn from "../../components/dutch/SquareBtn/SquareBtn";

import Product from "../../components/dutch/Product/Product";
import backImg from "./../../assets/image/dutchheader.png";
import { useEffect, useState } from "react";
import axios from "axios";
import apiClient from "../../axios";
import DutchComplite from "../../pages/DutchComplite/DutchComplite";

import { useNavigate } from "react-router-dom";

apiClient.get("/endpoint"); // https://your-api-base-url.com/endpoint

import { Client } from "@stomp/stompjs";
import { PATH } from "../../constants/path";
import { useAuthStore } from "../../store/AuthStore";

import {
  WaitingWrapper,
  WaitingTop,
  WaitingMain,
  Wrapper,
  Top,
  Title,
  Timer,
  ProcessContainer,
  ProcessLine,
  Process,
  Step,
  JoinBtnDiv,
  Main,
  DutchWaiting,
  DutchFin,
  FinContent,
  Bottom,
  Btn,
  BackImg,
} from "./DutchInvite.styles";

interface DutchPayItem {
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number;
  status: string;
}

interface ParticipantInfo {
  index: number;
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number | null;
  status: string;
}

const DutchInvite = () => {
  const { id, accessToken, name, phoneNumber } = useAuthStore();
  const nav = useNavigate();

  // const { name, id } = useAuthStore();
  console.log("이것도 뜨나요?");

  // 테스트용 멤버 데이터
  const testUser = {
    name: name,
    phoneNumber: phoneNumber,
    memberId: id,
  };

  const [dutchParticipants, setDutchParticipants] = useState<ParticipantInfo[]>(
    []
  ); // join 및 leave 후 남아있는 참여자 정보를 받을 변수
  const [dutchPayListInfo, setDutchPayListInfo] = useState<DutchPayItem[]>([]); // dutch 초대 첫 화면(대기화면)에서 더치페이 방 정보를 불러와 담을 변수

  const [isAccept, setIsAccept] = useState<boolean>(false); // 수락 버튼을 눌렀는지 확인용
  const [isOpen, setIsOpen] = useState<boolean>(false); // 더치페이 나가기 모달 상태 관리
  const [timeLeft, setTimeLeft] = useState<number>(2400); // 40분(2400초) 카운트다운을 위한 상태 관리
  const [process, setProcess] = useState<number>(0); // 진행 단계
  const [isHost, setIsHost] = useState<boolean>(false);
  const [dutchStart, setDutchStart] = useState<boolean>(false);
  const [maxMember, setMexMember] = useState<number | string>("");

  // 금액을 local storage에 저장하는 방식으로 해결해야할 것 같음...
  const [confirmAmount, setConfirmAmount] = useState<number>(0); // 참가자 확정 금액

  const [stop, setStop] = useState(false);
  const navigate = useNavigate();

  // 더치페이 방 입장 관련
  const [roomId, setRoomId] = useState<string>(
    "ba8deac8-2bb5-40a2-b21f-8cb7d7f13468"
  ); // 방 ID
  const [memberId, setMemberId] = useState<string | null>(
    "01923d9f-7b3d-78dd-9f9d-32f85c64fbcd"
  ); // 멤버 ID
  const [roomInfo, setRoomInfo] = useState<DutchPayInfo | undefined>(undefined); // 방 정보
  const [stompClient, setStompClient] = useState<Client | null>(null); // STOMP 클라이언트

  const [orderId, setOrderId] = useState<string>(
    "01923d9f-7b3d-70e9-ad8d-68a3ab09d578"
  ); // 주문 ID
  const [merchantId, setMerchantId] = useState<string>(
    "01923d9f-7b3d-7a9e-a0b3-24d7970f90d4"
  ); // 상점 ID
  const [merchantName, setMerchantName] = useState<string>("Example Merchant"); // 상점 이름
  const [merchantThumbnailUrl, setMerchantThumbnailUrl] = useState<string>("");
  const [categoryId, setCategoryId] = useState<string>("category"); // 카테고리 ID
  const [totalPrice, setTotalPrice] = useState<number>(10000); // 총 가격
  const [memberName, setMemberName] = useState<string | null>("");

  const [requestId, setRequestId] = useState<string>("");

  const [loading, setLoading] = useState(true); // 로딩 상태 관리


  useEffect(() => {
    console.log(testUser);
    setMemberName(testUser.name);
    setMemberId(testUser.memberId);

    // Url로부터 더치페이 입장 roomId 가져오기
    const currentUrl = window.location.href; // 현재 URL을 가져옴
    const parts = currentUrl.split("/"); // '/'를 기준으로 URL을 분할
    console.log(parts);
    const joinRoomId = parts[parts.length - 1]; // 마지막 요소가 roomId
    const maxMemNum = parts[parts.length - 2]; // 마지막에서 두 번째 요소가 MaxMember 수
    const requestID = parts[parts.length - 3]; // 마지막에서 세 번째 요소가 requestId
    const merchantID = parts[parts.length - 4]; // 마지막에서 네 번째 요소가 merchantId
    const categoryID = parts[parts.length - 5]; // 마지막에서 다섯 번째 요소가 categoryId
    const totalPRICE = parts[parts.length - 6]; // 마지막에서 여섯 번째 요소가 totalPrice
    const orderID = parts[parts.length - 7]; // 마지막에서 여섯 번째 요소가 orderId

    localStorage.setItem("orderId", orderID);
    localStorage.setItem("merchantId", merchantID);
    localStorage.setItem("requestId", requestID);
    localStorage.setItem("totalPrice", totalPRICE);
    localStorage.setItem("categoryId", categoryID);

    console.log("joinRoomId : ", joinRoomId); // "76735551-df91-4f94-9bc5-cc1895587aaf"
    setRoomId(joinRoomId);
    setMexMember(Number(maxMemNum));
    setRequestId(requestID);
    setMerchantId(merchantID);
    setCategoryId(categoryID);
    console.log(totalPRICE);
    setTotalPrice(Number(totalPRICE));
    setOrderId(orderID);

    //상품정보 불러오기
    loadProduct(orderID);

    getRoomInfo(joinRoomId); // 방 정보 가지고 오기
  }, []);

  useEffect(() => {
    // 만약 isHost === true이면 setProcess(3), setIsAccept(true);
  }, []);

  const goComplite = () => {
    nav(`/dutch-result/${roomId}`);
  };

  // useEffect(() => {
  //   if (dutchParticipants[0].status === "PROGRESS") {
  //     setProcess(1)
  //   }
  // }, [roomInfo])
  //   useEffect(() => {
  //     if (dutchParticipants.length > 0 && dutchParticipants[0].status === "READY") {
  //         setProcess(1);
  //     }
  // }, [roomInfo]);

  const getRoomInfo = async (roomid: string) => {
    try {
      const response = await apiClient.get(
        // `http://localhost:18020/moapay/core/dutchpay/getDutchRoomInfo/` +
        //   roomid,
        `api/moapay/core/dutchpay/getDutchRoomInfo/` + roomid,
        {
          withCredentials: true,
        }
      );
      // dutchPayList만 추출하여 상태 업데이트
      const dutchPayList = response.data.data.dutchPayList;
      setDutchPayListInfo(dutchPayList);
      // console.log('참여중인 사람', response.data)
      //   const PropsParticipants = dutchPayList.map((dutchParticipant, index) => ({
      //     index,
      //     uuid: dutchParticipant.uuid,
      //     memberId: dutchParticipant.memberId,
      //     memberName: dutchParticipant.memberName,
      //     amount: null, // 초기값은 null로 설정 (이후 설정 가능)
      //     status: dutchParticipant.status, // 서버 응답에서 status 필드를 추가
      // }))
      //   setDutchParticipants(PropsParticipants)
      console.log("참여중인 사람", response.data.data.dutchPayList);

      localStorage.setItem("dutchRoomId", roomid);

      for (let i = 0; i < response.data.data.dutchPayList.length; i++) {
        if (response.data.data.dutchPayList[i].memberId == id) {
          localStorage.setItem(
            "dutchPayId",
            response.data.data.dutchPayList[i].uuid
          );
        }
      }
    } catch (err) {
      console.log("에러 발생:", err);
      console.log("getRoomInfo-방 정보 조회 함수 에러");
    }
  };

  const onClickDutchStart = () => {
    setDutchStart(true);
    joinRoom();
  };

  const onClickAccept = () => {
    connectWebSocket(); // WebSocket 활성화
    joinRoom(); // 더치페이 방 입장

    localStorage.setItem("isHost", JSON.stringify(false)); // localStorage에 isHost 값 저장--> 언제 삭제할 건지 생각해보기
    // 삭제해야하는 상황: 모달을 통해 중단할 경우, stomp 중단할 경우......?그런데 host가 invite 페이지로 넘어갔을 때 process값이 2로 설정되어 있어야함....
    // const isHost = JSON.parse(localStorage.getItem('isHost') || 'false'); // localStorage에서 가져오는 코드
    // localStorage.removeItem('isHost'); // 삭제하는 코드

    setIsAccept(true);
    // nav(PATH.DUTCHPARTICIPATION) // 다음 화면으로 전환
  };

  const goHome = () => {
    nav(PATH.HOME);
  };

  // [하단]더치페이 참여자 페이지에 있던 것들
  // 더치페이 나가기 버튼 클릭 시 모달 띄우기
  const openModal = () => {
    console.log("더치페이 나가기 버튼 클릭");
    setIsOpen(true);
  };

  const closeModal = () => {
    setIsOpen(false);
  };

  // 10분 카운트다운 타이머
  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft((prevTime) => (prevTime > 0 ? prevTime - 1 : 0));
    }, 1000);

    // 타이머가 0이 되면 종료
    if (timeLeft === 0) {
      clearInterval(timer);
      alert("카운트다운이 완료되었습니다.");
      // 원하는 동작을 추가할 수 있습니다.
    }

    return () => clearInterval(timer); // 컴포넌트가 언마운트될 때 타이머 정리
  }, [timeLeft]);

  // 남은 시간을 분과 초로 변환하는 함수
  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes < 10 ? `0${minutes}` : minutes}:${
      seconds < 10 ? `0${seconds}` : seconds
    }`;
  };

  // 결제 버튼 클릭
  const onClickPaymentBtn = () => {
    // 결제하는 api 작성

    setProcess(3); // 다음 화면으로 전환
  };

  // 모달-더치페이 중단 버튼 클릭
  const onClickStop = () => {
    setStop(true);
  };

  useEffect(() => {
    console.log(`Process changed to ${process}`);
  }, [process]);

  const loadProduct = async (orderId: string) => {
    try {
      const response = await apiClient.get(
        `/api/moapay/core/dutchpay/orderInfo/${orderId}`,
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`, // Authorization 헤더에 Bearer 토큰 추가
          },
        }
      );
      console.log("상품 정보 조회 성공", response.data.data);
      const productInfo = response.data.data;
      setMerchantName(productInfo.itemNames[0]);
      setMerchantThumbnailUrl(productInfo.thumbnailUrl);
    } catch (error) {
      console.error("에러 발생", error);
      console.log("상품조회 실패");
    } finally {
      setLoading(false); // 로딩 완료
    }
  };

  // //////////////////////////////////////////////////////////////////
  const leaveRoom = () => {
    console.log("leave Room");
    if (!stompClient || !stompClient.connected || !roomId) return;

    const requestBody = {
      roomId: roomId,
      memberId: memberId,
    };

    stompClient.publish({
      destination: `/pub/dutchpay/leave/${roomId}`,
      body: JSON.stringify(requestBody),
    });

    console.log("Leave room:", roomId);
    nav(PATH.HOME);
  };

  const check = () => {
    console.log("check Room");
    if (!stompClient || !stompClient.connected || !roomId) {
      console.log("WebSocket not connected");
      return;
    }
    const requestBody = {
      memberId: memberId,
    };

    stompClient.publish({
      destination: `/pub/dutchpay/dutchRoom/${roomId}`,
    });

    console.log("confirm room:", roomId);
  };

  useEffect(() => {
    if (process === 6) {
      navigate("/dutch-result", { state: { roomId } });
    }
  }, [process, navigate, roomId]); // process와 roomId가 변경될 때마다 effect가 실행됩니다.

  const confirm = () => {
    console.log("confirm Room");
    if (!stompClient || !stompClient.connected || !roomId) {
      console.log("WebSocket not connected");
      return;
    }

    //TODO: dasfs
    const confirmPriceList = dutchParticipants.map((participant) => ({
      memberId: participant.memberId,
      price: participant.amount || 0, // amount 값이 null일 경우 0으로 대체
    }));

    // 요청 바디 구조 정의
    const requestBody = {
      roomId: roomId,
      memberCnt: dutchParticipants.length, // 실제 멤버 수를 여기에 설정
      confirmPriceDtos: confirmPriceList,
      // [
      //   {
      //     memberId: "01923d9f-7b3d-78dd-9f9d-32f85c64fbcd", // 실제 멤버 ID 설정
      //     price: 5000, // 실제 금액 설정
      //   },
      //   {
      //     memberId: "01923d9f-7b3d-70e9-ad8d-68a3ab09d578", // 두 번째 멤버 ID 설정
      //     price: 5000, // 실제 금액 설정
      //   },
      // ],
    };

    stompClient.publish({
      destination: `/pub/dutchpay/confirm/${roomId}`,
      body: JSON.stringify(requestBody),
    });

    console.log("confirm room:", roomId);
  };

  // 방 참여 함수 (STOMP로 메시지 보내기)
  const joinRoom = () => {
    console.log("join room : ", memberId);
    if (!stompClient || !stompClient.connected || !roomId) return;

    const requestBody = {
      memberId: memberId,
      memberName: memberName,
    };

    stompClient.publish({
      destination: `/pub/dutchpay/join/${roomId}`,
      body: JSON.stringify(requestBody),
    });

    console.log("Joining room:", roomId);

    // 서버의 응답을 받을 구독 설정
    stompClient.subscribe(`/sub/dutch-room/${roomId}`, (message) => {
      const response: ParticipantInfo[] = JSON.parse(message.body); // 서버에서 받은 응답 메시지를 JSON으로 파싱
      console.log("Participants received:", response);

      for (let i = 0; i < response.length; i++) {
        if (id == null) {
          localStorage.setItem("dutchPayId", response[1].uuid);
          break;
        }

        if (response[i].memberId == id) {
          localStorage.setItem("dutchPayId", response[i].uuid);
        }
      }

      // 필터링하여 필요한 정보만 포함하도록 가공
      const filteredParticipants = response.map((participant, index) => ({
        index,
        uuid: participant.uuid,
        memberId: participant.memberId,
        memberName: participant.memberName,
        amount: null, // 초기값은 null로 설정 (이후 설정 가능)
        status: participant.status,
      }));

      // 서버 응답을 dutchParticipants 상태에 저장
      setDutchParticipants(filteredParticipants);
      console.log("join 응답 확인용", filteredParticipants);
    });
  };

  const handleFinish = () => {
    console.log("결제 완료!");
    // 결제 완료 후 처리할 로직을 추가하세요.
    setProcess(5);
  };

  // WebSocket 연결 설정
  // TODO: 최현석
  const connectWebSocket = () => {
    const client = new Client({
      // brokerURL: "ws://localhost:18020/moapay/core/ws/dutchpay", // WebSocket URL
      brokerURL: "wss://j11c201.p.ssafy.io/api/moapay/core/ws/dutchpay", // WebSocket URL
      connectHeaders: {
        Authorization: "Bearer " + accessToken, // 여기에서 JWT 토큰을 추가합니다.
      },
      onConnect: (frame) => {
        console.log("Connected: " + frame);
        // 방 참여 시 메시지 구독
        client.subscribe(`/sub/dutch-room/${roomId}`, (message) => {
          console.log("Message received:", message.body);
          const roomData = JSON.parse(message.body);

          // 여기서 주최자가 보낸 메시지를 확인하여 참가자 process 값 업데이트
          if (roomData.status === "PROGRESS") {
            setProcess(1);
            getRoomInfo(roomId);
          }

          setRoomInfo(roomData); // 받은 메시지를 상태에 저장
        });
      },
      onStompError: (frame) => {
        console.error("Broker error: ", frame.headers["message"]);
      },
    });

    client.activate(); // 클라이언트 활성화
    setStompClient(client); // STOMP 클라이언트 저장
  };

  // 컴포넌트 언마운트 시 클라이언트 비활성화
  useEffect(() => {
    return () => {
      stompClient?.deactivate(); // 클라이언트 비활성화
    };
  }, [stompClient]);
  // //////////////////////////////////////////////////////////////////

  //TODO: 인터페이스 작성
  // UUID 타입을 나타내는 TypeScript 타입 정의 (필요에 따라 수정 가능)
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

  return (
    <>
      {isAccept ? (
        <Wrapper>
          <Top>
            <Title>
              <div>더치 페이</div>
              {/* 나가기 아이콘(-> 누르면 모달) */}
              {process < 2 && (
                <svg
                  onClick={openModal}
                  xmlns="http://www.w3.org/2000/svg"
                  x="0px"
                  y="0px"
                  width="32"
                  height="32"
                  viewBox="0 0 48 48"
                  fill="#656565"
                >
                  <path d="M 11.5 6 C 8.4802259 6 6 8.4802259 6 11.5 L 6 36.5 C 6 39.519774 8.4802259 42 11.5 42 L 29.5 42 C 32.519774 42 35 39.519774 35 36.5 A 1.50015 1.50015 0 1 0 32 36.5 C 32 37.898226 30.898226 39 29.5 39 L 11.5 39 C 10.101774 39 9 37.898226 9 36.5 L 9 11.5 C 9 10.101774 10.101774 9 11.5 9 L 29.5 9 C 30.898226 9 32 10.101774 32 11.5 A 1.50015 1.50015 0 1 0 35 11.5 C 35 8.4802259 32.519774 6 29.5 6 L 11.5 6 z M 33.484375 15.484375 A 1.50015 1.50015 0 0 0 32.439453 18.060547 L 36.878906 22.5 L 15.5 22.5 A 1.50015 1.50015 0 1 0 15.5 25.5 L 36.878906 25.5 L 32.439453 29.939453 A 1.50015 1.50015 0 1 0 34.560547 32.060547 L 41.560547 25.060547 A 1.50015 1.50015 0 0 0 41.560547 22.939453 L 34.560547 15.939453 A 1.50015 1.50015 0 0 0 33.484375 15.484375 z"></path>
                </svg>
              )}
            </Title>
            <Timer>
              {/* 10분 카운트다운 표시 */}
              <div>{formatTime(timeLeft)}</div>
            </Timer>

            <ProcessContainer>
              <ProcessLine></ProcessLine>
              <Process>
                <Step
                  style={{
                    fontSize: process === 0 ? "20px" : "17px",
                    fontWeight: process === 0 ? "700" : "none",
                    color: 0 < process ? "#868686" : "black",
                  }}
                >
                  <div
                    style={{
                      backgroundColor:
                        process === 0
                          ? "#8748F3"
                          : 0 < process
                          ? "black"
                          : "white",
                    }}
                  ></div>
                  <div>시작 대기</div>
                </Step>
                <Step
                  style={{
                    fontSize: process === 1 ? "20px" : "17px",
                    fontWeight: process === 1 ? "700" : "none",
                    color: 1 < process ? "#868686" : "black",
                  }}
                >
                  <div
                    style={{
                      backgroundColor:
                        process === 1
                          ? "#8748F3"
                          : 1 < process
                          ? "black"
                          : "white",
                    }}
                  ></div>
                  <div>금액 산정</div>
                </Step>
                <Step
                  style={{
                    fontSize: process === 2 ? "20px" : "17px",
                    fontWeight: process === 2 ? "700" : "none",
                    color: 2 < process ? "#868686" : "black",
                  }}
                >
                  <div
                    style={{
                      backgroundColor:
                        process === 2
                          ? "#8748F3"
                          : 2 < process
                          ? "black"
                          : "white",
                    }}
                  ></div>
                  <div>결제</div>
                </Step>
                <Step
                  style={{
                    fontSize: process === 3 || process === 4 ? "20px" : "17px",
                    fontWeight: process === 3 || process === 4 ? "700" : "none",
                    color: 4 < process ? "#868686" : "black",
                  }}
                >
                  <div
                    style={{
                      backgroundColor:
                        process === 3 || process === 4
                          ? "#8748F3"
                          : 4 < process
                          ? "black"
                          : "white",
                    }}
                  ></div>
                  <div>정산 대기</div>
                </Step>
                <Step
                  style={{
                    fontSize: process === 5 ? "20px" : "17px",
                    fontWeight: process === 5 ? "700" : "none",
                    color: 5 < process ? "#868686" : "black",
                  }}
                >
                  <div
                    style={{
                      backgroundColor:
                        process === 5
                          ? "#8748F3"
                          : 5 < process
                          ? "black"
                          : "white",
                    }}
                  ></div>
                  <div>완료</div>
                </Step>
              </Process>
            </ProcessContainer>
          </Top>

          <Main
            style={{
              backgroundColor:
                process === 2 ? "#B6BCFF" : "rgba(255, 255, 255, 0.65)",
            }}
          >
            <JoinBtnDiv>
              {process === 0 && !dutchStart ? (
                // <JoinBtnDiv><SquareBtn text="더치페이 참여하기" color='rgba(135, 72, 243, 0.74)' onClick={onClickDutchStart} /></JoinBtnDiv>
                <SquareBtn
                  text="더치페이 참여하기"
                  color="rgba(135, 72, 243, 0.74)"
                  onClick={onClickDutchStart}
                />
              ) : null}
            </JoinBtnDiv>
            {/* 3. 더치페이하는 상품 정보 */}
            {/* 2. 참여자 목록 컴포넌트_2단계인지 판단 기준: memberSetComplete === true */}
            {process < 2 ? (
              <Participant
                maxNum={Number(maxMember)}
                setDutchParticipants={setDutchParticipants}
                roomId={roomId}
                participants={dutchParticipants}
                leaveRoom={leaveRoom}
                confirm={confirm}
                setProcess={setProcess}
                process={process}
                roomInfo={roomInfo}
                setConfirmAmount={setConfirmAmount}
                totalPrice={totalPrice}
                isHostProp={false}
                merchantName={merchantName}
                merchantThumbnailUrl={merchantThumbnailUrl}
                loading={loading}
              />
            ) : null}
            {/* //TODO : 바꾸기 */}
            {process === 2 ? (
              <Payment
                onClick={onClickPaymentBtn}
                confirmAmount={confirmAmount}
                onFinish={handleFinish}
                merchantName={merchantName}
                merchantThumbnailUrl={merchantThumbnailUrl}
              />
            ) : null}
            {process === 3 ? (
              <DutchWaiting>
                <div>
                  <span>결</span>
                  <span>제</span>
                  <span> </span>
                  <span>진</span>
                  <span>행</span>
                  <span>중</span>
                  <span>.</span>
                  <span>.</span>
                  <span>.</span>
                </div>
                <div>결제 완료 시 웃는 얼굴로 변해요!</div>
              </DutchWaiting>
            ) : null}
            {process === 4 ? (
              <div>
                다른 사람 결제 대기 화면
                <div className="container">
                  <div id="spinner"></div>
                </div>
              </div>
            ) : null}
            {process === 5 ? (
              <DutchFin>
                <FinContent>
                  <div>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      fill="white"
                      x="0px"
                      y="0px"
                      width="48"
                      height="48"
                      viewBox="0 0 24 24"
                    >
                      <path d="M 20.738281 5.9941406 A 1.250125 1.250125 0 0 0 19.878906 6.3730469 L 9 17.234375 L 4.1152344 12.361328 A 1.250125 1.250125 0 1 0 2.3496094 14.130859 L 8.1171875 19.884766 A 1.250125 1.250125 0 0 0 9.8828125 19.884766 L 21.644531 8.140625 A 1.250125 1.250125 0 0 0 20.738281 5.9941406 z"></path>
                    </svg>
                  </div>
                  <div>결제 완료!</div>
                </FinContent>
                <Bottom>
                  <div style={{ display: "flex", gap: "10px", margin: "10px" }}>
                    <Btn onClick={goHome} style={{ fontSize: "15px" }}>
                      홈으로 돌아가기
                    </Btn>
                    <Btn onClick={goComplite} style={{ fontSize: "15px" }}>
                      더치페이 현황
                    </Btn>
                  </div>
                </Bottom>
              </DutchFin>
            ) : null}
            {/* //TODO: 여기 바꾸기 */}
          </Main>

          {/* <Bottom>
          <SquareBtn text={'더치페이 시작'} />
        </Bottom> */}

          {/* 배경 도형 */}
          <BackImg>
            <img src={triangle} />
            <img src={triangle} />
            <img src={triangle} />
          </BackImg>

          {/* [종료 버튼 미완]더치페이 나가기 모달 */}
          {isOpen && (
            <Modal isOpen={isOpen} onClose={closeModal}>
              <svg
                onClick={closeModal}
                xmlns="http://www.w3.org/2000/svg"
                x="0px"
                y="0px"
                width="20"
                height="20"
                viewBox="0 0 48 48"
              >
                <path d="M 38.982422 6.9707031 A 2.0002 2.0002 0 0 0 37.585938 7.5859375 L 24 21.171875 L 10.414062 7.5859375 A 2.0002 2.0002 0 0 0 8.9785156 6.9804688 A 2.0002 2.0002 0 0 0 7.5859375 10.414062 L 21.171875 24 L 7.5859375 37.585938 A 2.0002 2.0002 0 1 0 10.414062 40.414062 L 24 26.828125 L 37.585938 40.414062 A 2.0002 2.0002 0 1 0 40.414062 37.585938 L 26.828125 24 L 40.414062 10.414062 A 2.0002 2.0002 0 0 0 38.982422 6.9707031 z"></path>
              </svg>
              {stop ? (
                <div>정말 더치페이를 중단하시겠습니까?</div>
              ) : (
                <div>더치페이를 중단 시키시겠습니까?</div>
              )}
              <div>
                {/* <button onClick={closeModal}>취소</button> */}
                {/* 종료(중단)버튼: 더치페이 주최자는 더치페이가 모두에게 종료되도록하고 참가자는 참가자 본인만 종료되도록 해야함  */}
                {stop ? (
                  <button onClick={leaveRoom}>예</button>
                ) : (
                  <button onClick={onClickStop}>중단</button>
                )}
                {stop ? (
                  <button
                    onClick={() => {
                      setStop(false);
                    }}
                  >
                    취소
                  </button>
                ) : (
                  <div style={{ display: "flex", gap: "10px", margin: "10px" }}>
                    <button onClick={goHome} style={{ fontSize: "15px" }}>
                      홈으로 돌아가기
                    </button>
                    <button onClick={goComplite} style={{ fontSize: "15px" }}>
                      더치페이 현황
                    </button>
                  </div>
                )}
              </div>
            </Modal>
          )}
        </Wrapper>
      ) : (
        <WaitingWrapper>
          <WaitingTop>
            <img src={backImg} />
            <div>
              {/* 상품 정보는 또 어떻게 들고와야하지... */}
              <Product
                productName={merchantName}
                productUrl={
                  "https://www.ssg.com/item/itemView.ssg?itemId=1000566517100"
                }
                productImg={merchantThumbnailUrl}
              />
            </div>
          </WaitingTop>
          <WaitingMain>
            {/* 주최자 이름을 어떻게 들고와야 하는가... */}
            {dutchPayListInfo[0] ? (
              <div>'{dutchPayListInfo[0].memberName}'님이</div>
            ) : <div></div>}
            {/* <div>'' 님이</div> */}
            <div>더치페이를</div>
            <div>신청했습니다.</div>
            <div>
              {/* [미완]_수락 버튼 클릭시 어떤 방식으로 처리할 건지 더 생각해보기-> 참가자 전용 진행 페이지 따로? 참가자&주최자 같은 /dutch에서 진행하되 변수로 구분하여 분리...? */}
              <button onClick={onClickAccept}>수락</button>
              <button onClick={goHome}>취소</button>
            </div>
          </WaitingMain>
        </WaitingWrapper>
      )}
    </>
  );
};

export default DutchInvite;
