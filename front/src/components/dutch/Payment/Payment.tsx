import Product from "../Product/Product";
// import SquareBtn from "../SquareBtn/SquareBtn";
import { useState, useEffect } from "react";

import SelectCardList from "./../SelectCardList/SelectCardList";
import Modal from "../Modal/Modal";
import line from "./../../../assets/image/dutch_line_white.png";
import testCard from "./../../../assets/image/cards/신용카드이미지/12_올바른_FLEX_카드.png";
import testCard2 from "./../../../assets/image/cards/신용카드이미지/14_JADE_Classic.png";
import { Card } from "../../../store/CardStore";
import { useCardStore } from "../../../store/CardStore";
import { useAuthStore } from "../../../store/AuthStore";
import { v4 as uuidv4 } from "uuid"; // ES Modules

import {
  Wrapper,
  Price,
  CardView,
  CardInfo,
  Bottom,
  Btn,
  SelectModal,
} from "./Payment.styles";
import axios from "axios";
import apiClient from "../../../axios";

apiClient.get("/endpoint"); // https://your-api-base-url.com/endpoint

const testMainCard = {
  index: 0,
  img: testCard,
  name: "올바른 FLEX 카드",
};
const testMainCard2 = {
  index: 1,
  img: testCard2,
  name: "올바른 FLEX 카드",
};

interface PaymentProps {
  onClick: () => void;
  confirmAmount: number;
  onFinish: () => void;
  merchantName: string;
  merchantThumbnailUrl: string;
}

const Payment = ({ onClick, confirmAmount, onFinish, merchantName, merchantThumbnailUrl }: PaymentProps) => {
  const { accessToken, id, paymentType } = useAuthStore();
  const cardList = useCardStore((state) => state.cardList);

  const [rotate, setRotate] = useState(false);
  const [selectedCard, setSelectedCard] = useState<Card | null>(); // 선택된 카드(결제 카드)
  const [isOpen, setIsOpen] = useState<boolean>(false);

  const [orderId, setOrderId] = useState<string>(
    "01923d9f-7b3d-70e9-ad8d-68a3ab09d578"
  ); // 주문 ID
  const [totalPrice, setTotalPrice] = useState<number>(10000); // 총 가격
  const [categoryId, setCategoryId] = useState<string>("category"); // 카테고리 ID
  const [merchantId, setMerchantId] = useState<string>(
    "01923d9f-7b3d-7a9e-a0b3-24d7970f90d4"
  ); // 상점 ID
  const [requestId, setRequestId] = useState<string>("");
  const [dutchPayId, setDutchPayId] = useState<string>("");
  const [dutchRoomId, setDutchRoomId] = useState<string>("");

  useEffect(() => {
    // totalPrice는 숫자형으로 변환하되, 유효하지 않을 경우 0으로 설정
    const storedTotalPrice = parseInt(
      localStorage.getItem("totalPrice") || "0",
      10
    );
    const validatedTotalPrice = isNaN(storedTotalPrice) ? 0 : storedTotalPrice;

    // localStorage에서 값 가져오기
    setOrderId(localStorage.getItem("orderId") || "");
    setTotalPrice(validatedTotalPrice);
    setCategoryId(localStorage.getItem("categoryId") || "");
    setMerchantId(localStorage.getItem("merchantId") || "");
    setRequestId(settingStoreRequestId() || "");

    setDutchRoomId(localStorage.getItem("dutchRoomId") || "");
    // 값 로그로 출력
    const path = window.location.pathname; // 예: /items/10000/details
    const segments = path.split("/"); // '/'로 구분하여 배열로 변환

    console.log("Order ID:", localStorage.getItem("orderId"));
    console.log("Total Price:", localStorage.getItem("totalPrice"));
    console.log("Category ID:", localStorage.getItem("categoryId"));
    console.log("Merchant ID:", localStorage.getItem("merchantId"));
    console.log("Request ID:", localStorage.getItem("requestId"));

    const priceString = segments[4]; // segments[4]가 가격이 됩니다.

    setSelectedCard(cardList[0]);

    if (priceString) {
      const price = Number(priceString); // 문자열을 숫자로 변환
      if (!isNaN(price)) {
        setTotalPrice(price); // totalPrice 설정
      }
    }
  }, []);

  const settingStoreRequestId = (): string => {
    const newRequestId = uuidv4();
    localStorage.setItem("requestId", newRequestId);
    return newRequestId;
  };

  // 카드 가로, 세로 길이에 따른 회전 여부 판단 핸들러
  const handleImageLoad = (
    event: React.SyntheticEvent<HTMLImageElement, Event>
    // index: number
  ) => {
    const imgElement = event.currentTarget;
    if (imgElement.naturalWidth < imgElement.naturalHeight) {
      setRotate(true);
    }
  };

  // 다른 카드 선택하기 버튼을 눌렀을 경우
  const onClickChangeCard = () => {
    setIsOpen(true);
  };

  const pay = async () => {
    console.log("sse 연결시작!!!!!");
    console.log("requestId ", requestId);
    //페이먼트 연결
    const dutchPayId = localStorage.getItem("dutchPayId") || "";
    console.log(selectedCard?.cardNumber);
    console.log(selectedCard?.cvc);

    onClick();

    const eventSource = new EventSource(
      // `http://localhost:18010/moapay/pay/notification/subscribe/${requestId}`
      `https://j11c201.p.ssafy.io/api/moapay/pay/notification/subscribe/${requestId}`
    );

    eventSource.onopen = async () => {
      await console.log(
        "==============pay - SSE connection opened!=============="
      );
      console.log(eventSource);
    };

    // 'payment-completed' 이벤트를 수신할 때 실행될 로직 (이벤트 이름을 'payment-completed'로 변경)
    // 결제 완료를 보내주는 것
    eventSource.addEventListener("payment-completed", (event) => {
      console.log("Received 'sse' event:", event.data, "");
      try {
        const data = JSON.parse(event.data);
        console.log("Parsed data: ", data);
        // setPaymentResult(data);

        if (data.status === "SUCCEED") {
          console.log("success pay");
          onFinish();
        }

        // 결제가 완료된 후에는 loading 을 false로 변경하고
        // setIsLoading(false);

        //결과를 보여줄 수 있도록 isEnd를 true로 변경
        setTimeout(() => {
          // setIsEnd(true);
        }, 2000); // 2000 밀리초 = 2초
        // 결제 requestId 삭제하기
        localStorage.removeItem("requestId");
        //결과 담기
      } catch (error) {
        console.error("Data is not valid JSON:", event.data);
        // 만약 데이터가 JSON이 아니라 문자열인 경우 그대로 저장
      }
    });

    // 'sse' 이벤트를 수신할 때 실행될 로직
    eventSource.addEventListener("sse", (event) => {
      console.log("Received 'sse' event:", event.data);
      try {
        const data = JSON.parse(event.data);
        console.log("Parsed data: ", data);
      } catch (error) {
        console.error("Data is not valid JSON:", event.data);
      }
    });

    try {
      // setIsLoading(true);

      console.log(
        "dutchPayID : ",
        dutchPayId,
        "dutchRoomId : ",
        dutchRoomId,
        "requestId : ",
        requestId,
        "orderId : ",
        orderId,
        "merchantId : ",
        merchantId,
        "categoryId : ",
        categoryId,
        "id : ",
        id
      );

      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/core/generalpay/pay`,
        `/api/moapay/core/dutchpay/payment`,
        // `http://localhost:8765/moapay/core/generalpay/pay`,
        // `http://localhost:18020/moapay/core/dutchpay/payment`,
        {
          //TODO : 더치페이 아이디랑 더치 룸 아이디 가져와야함
          dutchPayId: dutchPayId,
          dutchRoomId: dutchRoomId,
          requestId: requestId,
          orderId: orderId,
          merchantId: merchantId,
          categoryId: categoryId,
          totalPrice: confirmAmount,
          memberId: id,
          cardSelectionType: "DUTCHPAY",
          // recommendType: paymentType || "PERFORM", // RECOMMEND인 경우 사용, BENEFIT / PERFORM
          cardNumber: selectedCard?.cardNumber, // FIX인 경우 사용
          cvc: selectedCard?.cvc, // FIX인 경우 사용
        },
        // {
        //   requestId: storedRequestId,
        //   orderId: storedOrderId,
        //   merchantId: storedMerchantId,
        //   categoryId: storedCategoryId,
        //   totalPrice: parseInt(storedTotalPrice, 10),
        //   memberId: id,
        //   cardSelectionType: "FIX",
        //   recommendType: "BENEFIT", // RECOMMEND인 경우 사용, BENEFIT / PERFORM
        //   cardNumber: "3998541707334420", // FIX인 경우 사용
        //   cvc: "123", // FIX인 경우 사용
        // },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response);
    } catch (e) {
      console.log(e);
    }
  };

  // 결제할 카드를 선택했을 경우
  const onSelectCard = (card: Card) => {
    console.log("selected Card : ", card);

    setSelectedCard(card);
    setIsOpen(false);
  };

  const onClose = () => {
    setIsOpen(false);
  };

  return (
    <Wrapper>
      {/* 상품 이름 및 경로를 어떻게 가져올 것인지에 대해서 생각해봐야 함 */}
      <Product
        productName={merchantName}
        productUrl={
          "https://www.ssg.com/item/itemView.ssg?itemId=1000566517100"
        }
        productImg={merchantThumbnailUrl}
      />

      {/* <div>총 금액: {prduct_price}원</div> */}
      <Price>총 금액: {totalPrice.toLocaleString()} 원</Price>

      {/* 구분 점선 */}
      <img src={line} style={{ width: "100%" }} />

      <CardView>
        <CardInfo>
          <img
            onLoad={(event) => handleImageLoad(event)} // 이미지가 로드되면 handleImageLoad 호출
            style={{
              // position: "absolute", 19 30 38 60 9.5 15
              width: rotate ? "47.5px" : "75px", // 회전 여부에 따라 width와 height 변경
              height: rotate ? "75px" : "47.5px",
              transform: rotate ? "rotate(-90deg)" : "none", // 회전시키기
              marginLeft: rotate ? "17.5px" : "0",
            }}
            src={`/assets/image/longWidth/신용카드이미지/${selectedCard?.cardProduct.cardProductImgUrl}.png`}
          />
          <div>{selectedCard?.cardProduct.cardProductName}</div>
        </CardInfo>
        <div onClick={onClickChangeCard}>다른카드 선택하기</div>
        <Bottom>
          {/* 결제 금액 넘겨 받아야 함 */}
          <Btn onClick={pay}>{confirmAmount.toLocaleString()}원 결제하기</Btn>
          {/* text={'7,000원 결제하기'} color={'white'} onClick={onClick} /> */}
        </Bottom>
      </CardView>

      <Modal isOpen={isOpen} onClose={onClose}>
        <SelectModal>
          <div>결제 카드 선택</div>
          <SelectCardList onSelectCard={onSelectCard} />
        </SelectModal>
      </Modal>
    </Wrapper>
  );
};

export default Payment;
