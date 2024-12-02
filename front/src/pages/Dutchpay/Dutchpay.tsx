import triangle from "./../../assets/image/triangle.png";
import Participant from "../../components/dutch/Participant/Participant";
import Modal from "../../components/dutch/Modal/Modal";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { Client } from "@stomp/stompjs";
import { useAuthStore } from "../../store/AuthStore";
import Payment from "../../components/dutch/Payment/Payment";
import apiClient from "../../axios";
import DutchComplite from "../../pages/DutchComplite/DutchComplite";
import { v4 as uuidv4 } from "uuid"; // ES Modules

apiClient.get("/endpoint"); // https://your-api-base-url.com/endpoint

import {
  Wrapper,
  Top,
  Title,
  LinkBox,
  CopyIcon,
  RequestUrl,
  ShareUrl,
  Main,
  BackImg,
  InviteTop,
  InviteTitle,
  Timer,
  ProcessContainer,
  ProcessLine,
  Process,
  Step,
  InviteMain,
  DutchWaiting,
  DutchFin,
  FinContent,
  Bottom,
  Btn,

  // process 4를 위한 css
  ParticipantTitle,
  PartiList,
  PartiInfo,
} from "./Dutchpay.styles";
import { useEffect, useState, useRef } from "react";
import { PATH } from "../../constants/path";

interface ParticipantInfo {
  index: number;
  uuid: string;
  memberId: string;
  memberName: string;
  amount: number | null;
  status: string;
}

// interface confirmPriceInfo {
//   memberId: string;
//   price: number
// }

const Dutchpay = () => {
  const nav = useNavigate();
  // const location = useLocation();

  const { name, id, accessToken } = useAuthStore();

  console.log("이름", name, "아이디", id);
  // console.log("Url 넘어오는지 확인용", location.state)

  // location.state가 없을 경우 localStorage에서 값을 가져옴
  // const joinUrl = location.state?.joinUrl || localStorage.getItem('joinUrl') || '';
  // console.log("Received joinUrl:", joinUrl)
  const [process, setProcess] = useState<number>(0); // 진행 단계
  const [timeLeft, setTimeLeft] = useState<number>(2400); // 40분(2400초) 카운트다운을 위한 상태 관리
  const timerRef = useRef<NodeJS.Timeout | null>(null); // 타이머를 저장할 ref

  const [isOpen, setIsOpen] = useState<boolean>(false); // 더치페이 나가기 모달 상태 관리
  const [isCompleteSettingCheck, setIsCompleteSettingCheck] =
    useState<boolean>(true); // 더치페이 인원 설정 완료 확인용 모달 띄우기 판단용
  const [memberNum, setMemberNum] = useState(""); // 참여자 수 입력 받는 변수
  const [dutchUrl, setDutchUrl] = useState<string>(""); // [[나중에 ''로 바꾸기 !!!!! ]더치페이 초대 url
  const [memberSetComplete, setMemberSetComplete] = useState<boolean>(false); // 참여자수 설정 완료 여부 판단용
  const [stop, setStop] = useState<boolean>(false); // 더치페이 중단하기 버튼을 눌렀는지의 여부를 판단

  const [webSocketJoinStep, setWebSocketJoinStep] = useState<number>(0); // 수작업 웹소켓 연결 및 방 참여를 위한 변수...
  const [dutchParticipants, setDutchParticipants] = useState<ParticipantInfo[]>(
    []
  ); // join 및 leave 후 남아있는 참여자 정보를 받을 변수

  const [confirmAmount, setConfirmAmount] = useState<number>(0); // 주최자 확정 금액
  // console.log(joinUrl)
  // useEffect (() => {
  //   if (joinUrl) {
  //     setDutchUrl(joinUrl)
  //     console.log("Dutch URL set to:", joinUrl)
  //   }
  // }, [joinUrl])

  // 테스트용 변수... 나중에 지울 예정(host)
  const [isHost, setIsHost] = useState<boolean>(true); // 쓰진 않을 것 같음...

  // // 참여자 수 바인딩
  // const onChangeMember = (e: React.ChangeEvent<HTMLInputElement>) => {
  //   console.log("확인용",e.target.value)
  //   setMemberNum(e.target.value)
  // }

  // // 참여자 수 입력 후 완료 버튼을 눌렀을 경우=>설정 완료로 변경
  // const onCheckComplete = () =>{
  //   setIsCompleteSettingCheck(true)
  // }

  // // [미완]더치페이 링크를 받아오는 api 요청(요청 방식 나중에 다시 확인하기)
  // const getDutchUrl = async () => {
  //   try {
  //     const response = await axios.get(
  //       `요청 api 주소 입력`,
  //       {
  //         headers: {
  //           "Content-Type": "application/json",
  //         }
  //         // withCredentials: true, // 쿠키를 포함하여 요청 <----- 필요한가?
  //       },
  //     );
  //     if (response.status === 200) {
  //       console.log("더치페이 url 생성완료")
  //       // setDutchUrl(응답 데이터 중 더치페이 url 넣기)
  //     }
  //   } catch (err) {
  //     console.error("에러 발생", err);
  //   }
  // };

  type UUID = string; // UUID를 문자열로 간주

  // DutchPayList 아이템 인터페이스
  interface DutchPayItem {
    uuid: UUID;
    memberId: UUID;
    memberName: string;
    amount: bigint; // 또는 number, 필요에 따라 선택
    status: string;
  }

  // 메인 DutchPayInfo 인터페이스
  interface DutchPayInfo {
    dutchUuid: UUID;
    memberCnt: bigint; // 또는 number, 필요에 따라 선택
    orderId: UUID;
    merchantId: UUID;
    merchantName: string;
    categoryId: string;
    totalPrice: bigint; // 또는 number, 필요에 따라 선택
    dutchPayList: DutchPayItem[]; // DutchPayItem 배열
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

  //////////////////////////////////////////////////////////////////////////////////////////
  const [roomId, setRoomId] = useState<string>(""); // 방 ID
  const [memberId, setMemberId] = useState<string>(id || ""); // 멤버 ID
  const [joinUrl, setJoinUrl] = useState<string>(""); // 방 참여 URL
  const [roomInfo, setRoomInfo] = useState<DutchPayInfo>(); // 방 정보
  const [stompClient, setStompClient] = useState<Client | null>(null); // STOMP 클라이언트

  // 방 생성에 필요한 필드
  const [maxMember, setMemberCnt] = useState<number | string>(""); // 총원 수
  const [orderId, setOrderId] = useState<string>(
    "01923d9f-7b3d-70e9-ad8d-68a3ab09d578"
  ); // 주문 ID
  const [merchantId, setMerchantId] = useState<string>(
    "01923d9f-7b3d-7a9e-a0b3-24d7970f90d4"
  ); // 상점 ID
  const [merchantName, setMerchantName] = useState<string>("Example Merchant"); // 상점 이름
  const [merchantThumbnailUrl, setMerchantThumbnailUrl] = useState<string>("");
  const [categoryId, setCategoryId] = useState<string>("category"); // 카테고리 ID
  const [totalPrice, setTotalPrice] = useState<number>(0); // 총 가격
  // const [memberName, setMemberName] = useState<string>("유저이름");
  const [memberName, setMemberName] = useState<string>(name || "");

  const [requestId, setRequestId] = useState<string>("");

  const [orderInfo, setOrderInfo] = useState<OrderInfo | null>(null); // orderInfo 데이터를 저장하는 state
  const [loading, setLoading] = useState<boolean>(true); // 로딩 상태 관리


  // console.log("이것도뜨나?")

  // useEffect(() => {
  //   if (process === 6) {
  //     nav("/dutch-result", { state: { roomId } });
  //   }
  // }, [process, nav, roomId]); // process와 roomId가 변경될 때마다 effect가 실행됩니다.

  useEffect(() => {
    // totalPrice는 숫자형으로 변환하되, 유효하지 않을 경우 0으로 설정
    const storedTotalPrice = parseInt(
      localStorage.getItem("totalPrice") || "0",
      10
    );
    const validatedTotalPrice = isNaN(storedTotalPrice) ? 0 : storedTotalPrice;
    const orderIdInput = localStorage.getItem(localStorage.getItem("orderId") || "")
    // localStorage에서 값 가져오기
    setOrderId(localStorage.getItem("orderId") || "");
    setTotalPrice(validatedTotalPrice);
    setCategoryId(localStorage.getItem("categoryId") || "");
    setMerchantId(localStorage.getItem("merchantId") || "");
    setRequestId(settingStoreRequestId() || "");

    // 값 로그로 출력
    console.log("Order ID:", localStorage.getItem("orderId"));
    console.log("Total Price:", localStorage.getItem("totalPrice"));
    console.log("Category ID:", localStorage.getItem("categoryId"));
    console.log("Merchant ID:", localStorage.getItem("merchantId"));
    console.log("Request ID:", localStorage.getItem("requestId"));

    // 상품 정보 조회
    // loadProduct(localStorage.getItem("orderId") || "");
    loadProduct(orderIdInput || "");
  }, []);

  const settingStoreRequestId = (): string => {
    const newRequestId = uuidv4();
    localStorage.setItem("requestId", newRequestId);
    return newRequestId;
  };

  const goComplite = () => {
    console.log("dutchRoom ID : ", roomId);
    nav(`/dutch-result/${roomId}`);
  };

  // 방 생성 함수
  // TODO : 해결해주세요
  const createRoom = async () => {
    console.log("here... see !!!!!!!!!!!!!!");
    const requestBody = {
      memberId: memberId,
      maxMember: maxMember,
      orderId: orderId,
      merchantId: merchantId,
      merchantName: merchantName,
      categoryId: categoryId,
      totalPrice: totalPrice,
    };

    try {
      console.log(requestBody);

      const response = await apiClient.post(
        // "http://localhost:18020/moapay/core/dutchpay/createRoom",
        `/api/moapay/core/dutchpay/createRoom`,
        requestBody,
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`, // Authorization 헤더에 Bearer 토큰 추가
          },
        }
      );
      console.log("Room created:", response.data);

      // message.body를 DutchRoomMessage 타입으로 변환
      const parsedMessage: DutchRoomMessage = response.data;
      localStorage.setItem("dutchRoomId", parsedMessage.data);
      // const generatedUrl = `http://localhost:5173/dutchpay/invite/${orderId}/${totalPrice}/${categoryId}/${merchantId}/${requestId}/${maxMember}/${parsedMessage.data}`;
      const generatedUrl = `https://j11c201.p.ssafy.io/dutchpay/invite/${orderId}/${totalPrice}/${categoryId}/${merchantId}/${requestId}/${maxMember}/${parsedMessage.data}`;
      console.log("Generated joinUrl:", generatedUrl);
      // localStorage.setItem('joinUrl', generatedUrl);  // localStorage에 joinUrl 저장
      // setJoinUrl(parsedMessage.data); // 방 생성 후 반환된 URL 저장
      setJoinUrl(generatedUrl); // 더치페이 초대 url 저장.....?
      console.log("setRoomId : ", parsedMessage.data);
      setRoomId(parsedMessage.data); // 생성된 방의 roomId 저장

      // nav(PATH.DUTCHPAY, { state: { joinUrl: generatedUrl }}) // 인원 설정하여 방 생성 후 다음 페이지로 이동
      // connectWebSocket() // WebSocket연결해서 Stomp 실행
      // joinRoom() // 방 참여
      console.log("end...plz....");
    } catch (error) {
      console.error("Error creating room:", error);
    }
  };

  // 방 참여 함수 (STOMP로 메시지 보내기)
  const joinRoom = () => {
    console.log("join room");
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
      // console.log("Participants received:", response);

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

  const confirm = () => {
    console.log("confirm Room");
    if (!stompClient || !stompClient.connected || !roomId) {
      console.log("WebSocket not connected");
      return;
    }

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

    // 주최자가 결제 요청을 했음을 알리는 메시지 발행 (status: 'CONFIRM')
    stompClient.publish({
      destination: `/sub/dutch-room/${roomId}`, // 참가자가 구독 중인 경로
      body: JSON.stringify({ status: "PROGRESS" }),
    });

    console.log("컨펌 후 참가자 정보 확인", dutchParticipants);
    console.log("confirm room:", roomId);
  };

  // 서버로부터 받은 메시지의 타입 정의
  interface DutchRoomMessage {
    status: string;
    message: string;
    data: string; // UUID나 다른 타입에 맞게 수정 가능
  }

  // WebSocket 연결 설정
  const connectWebSocket = () => {
    console.log("connect websocket");
    const client = new Client({
      brokerURL: "wss://j11c201.p.ssafy.io/api/moapay/core/ws/dutchpay", // WebSocket URL
      connectHeaders: {
        Authorization: "Bearer " + accessToken, // 여기에서 JWT 토큰을 추가합니다.
      },
      onConnect: (frame) => {
        console.log("Connected: " + frame);

        // 방 참여 시 메시지 구독
        client.subscribe(`/sub/dutch-room/${roomId}`, (message) => {
          console.log("Message received:", message.body);
          setRoomInfo(JSON.parse(message.body)); // 받은 메시지를 상태에 저장
        });
      },
      onStompError: (frame) => {
        console.error("Broker error: ", frame.headers["message"]);
      },
    });

    client.activate(); // 클라이언트 활성화
    setStompClient(client); // STOMP 클라이언트 저장
    setTimeout(() => {
      console.log(roomInfo);
    }, 5000);
  };

  // 컴포넌트 언마운트 시 클라이언트 비활성화
  useEffect(() => {
    return () => {
      stompClient?.deactivate(); // 클라이언트 비활성화
    };
  }, [stompClient]);

  /////////////////////////////////////////////

  const loadProduct = async (orderId: string) => {
    console.log("불러올 상품 정보 아이디:", orderId)
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
      if (response?.status === 200){
        console.log("상품 정보 조회 성공", response.data.data);
        const productInfo = response.data.data;
        setMerchantName(productInfo.itemNames[0]);
        setMerchantThumbnailUrl(productInfo.thumbnailUrl);
        // 
        const orderData = response.data;
        setOrderInfo(orderData)
      }
    } catch (error) {
      console.error("에러 발생", error);
      console.log("상품조회 실패");
    } finally {
      setLoading(false); // 로딩 완료
    }
  };

  useEffect(() => {
    loadProduct(orderId);
  }, [orderId]);
  

  useEffect(() => {
    console.log("hello..?");
    console.log("Dutchpay 페이지 로드");
    const storedMemberNum = localStorage.getItem("maxMember");
    if (storedMemberNum) {
      setMemberCnt(Number(storedMemberNum));
      console.log("로컬 스토리지에서 maxMember 불러오기:", storedMemberNum);
    } else {
      console.log("로컬 스토리지에 maxMember 값이 없음");
    }

    setWebSocketJoinStep(0);
    // createRoom() // 방 생성

    // setTimeout(() => {
    //   createRoom() // 방 생성

    // }, 5000);

    // setJoinUrl(`http://localhost:5173/dutchpay/invite/${roomId}`)
    // connectWebSocket()
    // joinRoom()
    // check()
  }, []);

  // 10분 카운트다운 타이머
  useEffect(() => {
    // 타이머 설정
    timerRef.current = setInterval(() => {
      setTimeLeft((prevTime) => {
        if (prevTime <= 0) {
          clearInterval(timerRef.current!);
          alert("카운트다운이 완료되었습니다.");
          return 0;
        }
        return prevTime - 1;
      });
    }, 1000);

    // 컴포넌트가 언마운트될 때 타이머 정리
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, []);

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

    setProcess(4); // 다음 화면으로 전환
  };

  const finish = () => {
    setProcess(5);
  };

  const onClickRequestUrl = () => {
    connectWebSocket();
    loadProduct(orderId)
    setWebSocketJoinStep(2);
  };

  const goHome = () => {
    nav(PATH.HOME);
  };

  // 모달 취소 버튼...인원 설정 페이지로 돌아가기
  const goBack = () => {
    nav(PATH.DUTCHOPEN);
  };

  // 더치페이 나가기 버튼 클릭 시 모달 띄우기
  const openModal = () => {
    console.log("더치페이 나가기 버튼 클릭");
    setIsOpen(true);
  };

  const onClickAccept = () => {
    createRoom();
    setIsCompleteSettingCheck(false);
    setWebSocketJoinStep(1);

    localStorage.setItem("isHost", JSON.stringify(true)); // localStorage에 isHost 값 저장--> 언제 삭제할 건지 생각해보기
    // 삭제해야하는 상황: 모달을 통해 중단할 경우, stomp 중단할 경우......?그런데 host가 invite 페이지로 넘어갔을 때 process값이 2로 설정되어 있어야함....
    // const isHost = JSON.parse(localStorage.getItem('isHost') || 'false'); // localStorage에서 가져오는 코드
    // localStorage.removeItem('isHost'); // 삭제하는 코드
  };

  const closeModal = () => {
    setIsOpen(false);
  };
  const closeSettingModal = () => {
    setIsCompleteSettingCheck(false);
  };
  // const completeMemberSetting = () => {
  //   setMemberSetComplete(true)
  //   setIsCompleteSettingCheck(false);
  // };

  const onClickStop = () => {
    setStop(true);
  };

  // 더치페이 url을 클립보드에 복사하는 함수
  const copyToClipboard = () => {
    joinRoom();
    navigator.clipboard
      .writeText(joinUrl)
      .then(() => {
        console.log("url이 클립보드에 복사되었습니다.");
      })
      .catch((err) => {
        console.error("복사 실패:", err);
      });
    loadProduct(orderId)
  };

  // useEffect(()=>{
  // getDutchUrl() // 참가 인원을 입력할 경우 더치페이 링크를 받아오는 api 요청
  // }, [memberSetComplete]) // 참가 인원 세팅완료 여부 변수에 변화가 있을 때 조회가 되도록

  // useEffect(() => {
  //   setMemberSetComplete(false)
  // }, []) // 맨 처음 랜더링 시(더치페이를 처음으로 실행시킬 시) 값 초기화해야할 것들

  return (
    <Wrapper>
      {process < 2 ? (
        <>
          <Top>
            <Title>
              <div onClick={connectWebSocket}>더치 페이</div>
              {/* <div onClick={joinRoom}>테스트용</div> */}
              {/* 나가기 아이콘(-> 누르면 모달) */}
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
            </Title>
            <LinkBox>
              {/* {!memberSetComplete&&
                  <input value={memberNum} type="number" placeholder="인원을 설정해주세요." onChange={onChangeMember}/>
                } */}

              {/* 사용자가 인원을 입력했을 경우에만 다음 화살표(->누르면 재확인 모달)가 나타나도록 함 */}
              {/* {memberNum&&!memberSetComplete? 
                  <svg onClick={onCheckComplete} xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="24" height="24" viewBox="0 0 48 48" fill="#ffffff">
                    <path d="M 24 4 C 12.972066 4 4 12.972074 4 24 C 4 35.027926 12.972066 44 24 44 C 35.027934 44 44 35.027926 44 24 C 44 12.972074 35.027934 4 24 4 z M 24 7 C 33.406615 7 41 14.593391 41 24 C 41 33.406609 33.406615 41 24 41 C 14.593385 41 7 33.406609 7 24 C 7 14.593391 14.593385 7 24 7 z M 25.484375 16.484375 A 1.50015 1.50015 0 0 0 24.439453 19.060547 L 27.878906 22.5 L 16.5 22.5 A 1.50015 1.50015 0 1 0 16.5 25.5 L 27.878906 25.5 L 24.439453 28.939453 A 1.50015 1.50015 0 1 0 26.560547 31.060547 L 32.560547 25.060547 A 1.50015 1.50015 0 0 0 32.560547 22.939453 L 26.560547 16.939453 A 1.50015 1.50015 0 0 0 25.484375 16.484375 z"></path>
                  </svg>
                :
                  null
                } */}
              {
                // memberNum&&
                // memberSetComplete&&
                webSocketJoinStep > 1 && (
                  <CopyIcon>
                    <svg
                      onClick={copyToClipboard}
                      xmlns="http://www.w3.org/2000/svg"
                      width="24"
                      height="24"
                      fill="#7A7A7A"
                      viewBox="0 0 448 512"
                    >
                      <path d="M384 336l-192 0c-8.8 0-16-7.2-16-16l0-256c0-8.8 7.2-16 16-16l140.1 0L400 115.9 400 320c0 8.8-7.2 16-16 16zM192 384l192 0c35.3 0 64-28.7 64-64l0-204.1c0-12.7-5.1-24.9-14.1-33.9L366.1 14.1c-9-9-21.2-14.1-33.9-14.1L192 0c-35.3 0-64 28.7-64 64l0 256c0 35.3 28.7 64 64 64zM64 128c-35.3 0-64 28.7-64 64L0 448c0 35.3 28.7 64 64 64l192 0c35.3 0 64-28.7 64-64l0-32-48 0 0 32c0 8.8-7.2 16-16 16L64 464c-8.8 0-16-7.2-16-16l0-256c0-8.8 7.2-16 16-16l32 0 0-48-32 0z" />
                    </svg>
                  </CopyIcon>
                )
              }

              {webSocketJoinStep === 1 && (
                <RequestUrl onClick={onClickRequestUrl}>
                  터치하여 초대 url 발급받기
                </RequestUrl>
              )}
              {
                // memberNum&&
                // memberSetComplete&&
                webSocketJoinStep > 1 && (
                  <ShareUrl onClick={joinRoom}>{joinUrl}</ShareUrl>
                )
              }
            </LinkBox>
          </Top>

          <Main>
            {/* 3. 더치페이하는 상품 정보 */}
            {/* 2. 참여자 목록 컴포넌트_2단계인지 판단 기준: memberSetComplete === true */}
            {
              // memberSetComplete&&
              <Participant
                maxNum={Number(maxMember)}
                setDutchParticipants={setDutchParticipants}
                roomId={roomId}
                participants={dutchParticipants}
                leaveRoom={leaveRoom}
                confirm={confirm}
                setProcess={setProcess}
                process={process}
                setConfirmAmount={setConfirmAmount}
                totalPrice={totalPrice}
                isHostProp={true}
                merchantName={merchantName}
                merchantThumbnailUrl={merchantThumbnailUrl}
                loading={loading}
              />
            }
          </Main>
        </>
      ) : (
        <>
          <InviteTop>
            <InviteTitle>
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
            </InviteTitle>
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
          </InviteTop>
          <InviteMain
            style={{
              backgroundColor:
                process === 2 ? "#B6BCFF" : "rgba(255, 255, 255, 0.65)",
            }}
          >
            {/* //TODO : 바꾸기 */}
            {process === 2 ? (
              <Payment
                onClick={onClickPaymentBtn}
                confirmAmount={confirmAmount}
                onFinish={finish}
                merchantName={merchantName}
                merchantThumbnailUrl={merchantThumbnailUrl}
              />
            ) : null}
            {process === 4 ? (
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
            {process === 3 ? (
              <div>
                {/* 다른 사람 결제 대기 화면 */}
                {/* <div className="container">
                  <div id="spinner"></div>
                </div> */}
                <ParticipantTitle>
                  참여자({dutchParticipants.length}/{maxMember})
                </ParticipantTitle>
                {/* 참가자가 있을 경우에만 출력되도록 */}
                <PartiList>
                  {dutchParticipants.length > 0
                    ? dutchParticipants.map((participant, index) => (
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

                          {
                            // 이름을 중앙에 오게 하기 위해 추가한 꼼수div...
                            // (participant.index === 0 || !isHost) &&
                            <div></div>
                          }
                        </PartiInfo>
                      ))
                    : true}
                </PartiList>
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
            {/* //TODO: 여기도 바꿔야함 */}
            {process === 6 ? (
              <DutchComplite roomId={roomId}></DutchComplite>
            ) : null}
          </InviteMain>
        </>
      )}

      {/* 배경 도형 */}
      <BackImg>
        <img src={triangle} />
        <img src={triangle} />
        <img src={triangle} />
      </BackImg>

      {/* 모달 */}
      {/* 더치페이 인원 설정 확인용 모달 */}
      {isCompleteSettingCheck && (
        <Modal isOpen={isCompleteSettingCheck} onClose={closeSettingModal}>
          <div>
            {maxMember}명과 더치페이를<div style={{ height: "7px" }}></div>
            진행하시겠습니까?
          </div>
          <div>
            <button onClick={onClickAccept}>확인</button>
            <button onClick={goBack}>취소</button>
          </div>
        </Modal>
      )}

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
            <div>정말 더치페이를 중단시키겠습니까?</div>
          ) : (
            <div>더치페이를 중단 시키시겠습니까?</div>
          )}
          <div>
            {/* <button onClick={closeModal}>취소</button> */}
            {/* 종료(중단)버튼: 더치페이 주최자는 더치페이가 모두에게 종료되도록하고 참가자는 참가자 본인만 종료되도록 해야함  */}
            {stop ? (
              <button>예</button>
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
              <button onClick={goHome}>홈으로</button>
            )}
          </div>
        </Modal>
      )}
    </Wrapper>
  );
};
export default Dutchpay;
