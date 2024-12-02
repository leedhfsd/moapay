import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import {
  SelectView,
  Wrapper,
  Button,
  Title,
  Loading,
  Result,
  ResultBox,
  ResultBoxInner,
  CardImg,
  Content,
  Record,
  Etc,
  DotNav,
  HomeBtn,
} from "./SelectPaymentType.styles";
import { PATH } from "../../constants/path";
import { useEffect, useState } from "react";
import { v4 as uuidv4 } from "uuid"; // ES Modules
import axios from "axios";
import { useAuthStore } from "../../store/AuthStore";
import { Swiper as SwiperInstance } from "swiper/types"; // Swiper 타입 불러오기
import { Swiper, SwiperSlide } from "swiper/react";
// import { Pagination } from 'swiper';  // Pagination 모듈을 swiper 패키지에서 불러오기
// import 'swiper/swiper-bundle.min.css';
import "../../../node_modules/swiper/swiper-bundle.min.css";
import ParticleCanvas from "../../components/ParticleCanvas";
import { useCardStore } from "../../store/CardStore";
import BiometricsAuthModal from "../BiometricsAuthModal/BiometricsAuthModal";
import apiClient from "../../axios";
interface BenefitDetail {
  discount: number; // long, 할인 금액
  point: number; // long, 적립 포인트
  cashback: number; // long, 캐시백 금액
}

interface PaymentResultCardInfo {
  paymentId: string; // UUID, 결제 ID
  cardName: string; // String, 카드 이름
  imageUrl: string; // String, 카드 이미지 URL
  cardId: string; // UUID, 카드 ID
  cardNumber: string; // String, 카드 번호
  amount: number; // long, 결제 금액
  actualAmount: number; // long, 실제 결제 금액
  performance: number; // long, 카드 성능 (혜택 관련 성능 지표)
  usedAmount: number; // long, 사용 금액
  benefitActivated: boolean; // 혜택 활성화 여부
  benefitUsage: number; // long, 혜택 사용량
  benefitDetail: BenefitDetail; // 혜택 상세 정보
}

interface AppClientResponse {
  requestId: string; // UUID, 요청 ID
  paymentId: string; // UUID, 결제 ID
  merchantName: string; // String, 가맹점 이름
  totalPrice: number; // long, 총 결제 금액
  createTime: string; // LocalDateTime, 결제 생성 시간
  usedCardCount: number; // int, 사용된 카드 개수
  paymentResultCardInfoList: PaymentResultCardInfo[]; // 결제 카드 정보 리스트
}
/**
 *
 * QR을 찍으면 해당 페이지로 이동
 * sse 구독하기 - requestId false false
 * 결제수단 선택 후 결제하기를 누르면 결제 요청을 보냄  true false
 * sse로 결제 응답이 오면 isEnd true  true true
 * 딜레이 2초 걸어서 isLoading false
 * 결과뿌리기
 *
 *
 * isLoading, isEnd
 * false false=> 결제수단 고르기
 * true false => 결제 진행중
 * true true => 결제 완료 애니메이션
 * false true => 결제 완료  창
 */
const SelectPaymentType = () => {
  const [showModal, setShowModal] = useState<boolean>(false);
  const [isAuth, setIsAuth] = useState<boolean>(false);
  const { accessToken, id, paymentType } = useAuthStore();
  const { cardList } = useCardStore();
  const navigate = useNavigate();

  //인증이 끝났을때
  const endAuth = () => {
    setIsAuth(true);
    setShowModal(false);
  };

  // 쿼리 파라미터 값 읽기
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get("orderId");
  const totalPrice = searchParams.get("totalPrice");
  const categoryId = searchParams.get("categoryId");
  const merchantId = searchParams.get("merchantId");
  const QRCode = searchParams.get("QRCode");

  // 단일결제 = single , 분할결제 = multi , 분할결제 = dutch
  const [selectedPayType, setSelectedPayType] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isEnd, setIsEnd] = useState<boolean>(false);

  //구독 시 필요한 id
  const [requestId, setRequestId] = useState<string>("");

  //결제 결과
  const [paymentResult, setPaymentResult] =
    useState<AppClientResponse | null>();

  useEffect(() => {
    // orderId 저장
    if (orderId) {
      localStorage.setItem("orderId", orderId);
    }

    // totalPrice 저장
    if (totalPrice) {
      localStorage.setItem("totalPrice", totalPrice);
    }

    // categoryId 저장
    if (categoryId) {
      localStorage.setItem("categoryId", categoryId);
    }

    // merchantId 저장
    if (merchantId) {
      localStorage.setItem("merchantId", merchantId);
    }

    // QRCode 저장
    if (QRCode) {
      localStorage.setItem("QRCode", QRCode);
    }
    setRequestId(settingStoreRequestId());
  }, [orderId, totalPrice, categoryId, merchantId, QRCode]);
  /**
   *
   * requestId가 없을 경우 발급
   */
  const settingStoreRequestId = (): string => {
    const newRequestId = uuidv4();
    localStorage.setItem("requestId", newRequestId);
    return newRequestId;
  };

  /**
   * sse 구독하기
   */
  useEffect(() => {
    if (requestId) {
    }
  }, [requestId]);

  /**
   * 결제 시작 함수 - 모달을 연다. setShowModal(true)
   */
  const startPay = async () => {
    const storedRequestId = localStorage.getItem("requestId");
    const storedOrderId = localStorage.getItem("orderId");
    const storedMerchantId = localStorage.getItem("merchantId");
    const storedCategoryId = localStorage.getItem("categoryId");
    const storedTotalPrice = localStorage.getItem("totalPrice") || "0";
    const storedQRCode = localStorage.getItem("QRCode");
    console.log(
      storedRequestId,
      storedOrderId,
      storedMerchantId,
      storedCategoryId,
      storedTotalPrice
    );
    if (selectedPayType == "single") {
      setShowModal(true);
      // setIsAuth(true);
    } else if (selectedPayType == "multi") {
      // console.log("here");
      setShowModal(true);
      // setIsAuth(true);
    } else if (selectedPayType == "dutch") {
      //더치페이지로 이동할때 필요한 정보 들고 가세용
      navigate(PATH.DUTCHOPEN);
    }
  };

  /**
   * 결제 진행함수
   */
  const multiPaying = async () => {
    const storedRequestId = localStorage.getItem("requestId");
    const storedOrderId = localStorage.getItem("orderId");
    const storedMerchantId = localStorage.getItem("merchantId");
    const storedCategoryId = localStorage.getItem("categoryId");
    const storedTotalPrice = localStorage.getItem("totalPrice") || "0";
    const storedQRCode = localStorage.getItem("QRCode");
    console.log(
      storedRequestId,
      storedOrderId,
      storedMerchantId,
      storedCategoryId,
      storedTotalPrice
    );
    setIsLoading(true);
    console.log("sse 연결시작!!!!!");
    console.log("requestId ", requestId);
    //페이먼트 연결
    const eventSource = new EventSource(
      // `http://localhost:18010/moapay/pay/notification/subscribe/${requestId}}`
      `https://j11c201.p.ssafy.io/api/moapay/pay/notification/subscribe/${requestId}`
    );

    //페이 연결 열기
    eventSource.onopen = async () => {
      await console.log(
        "==============pay - SSE connection opened!=============="
      );

      console.log("이벤트 응답 : ", eventSource);
    };

    // 'payment-completed' 이벤트를 수신할 때 실행될 로직 (이벤트 이름을 'payment-completed'로 변경)
    // 결제 완료를 보내주는 것
    eventSource.addEventListener("payment-completed", (event) => {
      console.log("Received 'sse' event:", event.data, "");
      try {
        const data = JSON.parse(event.data);
        console.log("Parsed data: ", data);
        setPaymentResult(data);

        // 결제가 완료된 후에는 loading 을 false로 변경하고
        setTimeout(() => {
          setIsEnd(true);
        }, 2300); // 2000 밀리초 = 2초
        //결과를 보여줄 수 있도록 isEnd를 true로 변경

        setTimeout(() => {
          setIsLoading(false);
        }, 3300);
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

    // 페이에서 에러 발생 시 실행될 로직
    eventSource.onerror = async (e) => {
      await console.log("Error with pay SSE", e);
      // 에러가 발생하면 SSE를 닫음
      eventSource.close();
    };

    try {
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/core/generalpay/pay`,
        `/api/moapay/core/generalpay/pay`,
        // `http://localhost:8765/moapay/core/generalpay/pay`,
        {
          requestId: storedRequestId,
          orderId: storedOrderId,
          merchantId: storedMerchantId,
          categoryId: storedCategoryId,
          totalPrice: parseInt(storedTotalPrice, 10),
          memberId: id,
          cardSelectionType: "RECOMMEND",
          recommendType: paymentType || "PERFORM", // RECOMMEND인 경우 사용, BENEFIT / PERFORM
          cardNumber: cardList[0].cardNumber, // FIX인 경우 사용
          cvc: cardList[0].cvc, // FIX인 경우 사용
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
      console.log("결제 응답 : ", response);
    } catch (e) {
      console.log(e);
    }
  };

  const singlePaying = async () => {
    const storedRequestId = localStorage.getItem("requestId");
    const storedOrderId = localStorage.getItem("orderId");
    const storedMerchantId = localStorage.getItem("merchantId");
    const storedCategoryId = localStorage.getItem("categoryId");
    const storedTotalPrice = localStorage.getItem("totalPrice") || "0";
    const storedQRCode = localStorage.getItem("QRCode");
    console.log(
      storedRequestId,
      storedOrderId,
      storedMerchantId,
      storedCategoryId,
      storedTotalPrice
    );
    setIsLoading(true);
    console.log("sse 연결시작!!!!!");
    console.log("requestId ", requestId);
    //페이먼트 연결
    const eventSource = new EventSource(
      // `http://localhost:18010/moapay/pay/notification/subscribe/${requestId}}`
      `https://j11c201.p.ssafy.io/api/moapay/pay/notification/subscribe/${requestId}`
    );

    //페이 연결 열기
    eventSource.onopen = async () => {
      await console.log(
        "==============pay - SSE connection opened!=============="
      );

      console.log("이벤트 응답 : ", eventSource);
    };

    // 'payment-completed' 이벤트를 수신할 때 실행될 로직 (이벤트 이름을 'payment-completed'로 변경)
    // 결제 완료를 보내주는 것
    eventSource.addEventListener("payment-completed", (event) => {
      console.log("Received 'sse' event:", event.data, "");
      try {
        const data = JSON.parse(event.data);
        console.log("Parsed data: ", data);
        setPaymentResult(data);

        // 결제가 완료된 후에는 loading 을 false로 변경하고
        setTimeout(() => {
          setIsEnd(true);
        }, 2300); // 2000 밀리초 = 2초
        //결과를 보여줄 수 있도록 isEnd를 true로 변경

        setTimeout(() => {
          setIsLoading(false);
        }, 3300);
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

    // 페이에서 에러 발생 시 실행될 로직
    eventSource.onerror = async (e) => {
      await console.log("Error with pay SSE", e);
      // 에러가 발생하면 SSE를 닫음
      eventSource.close();
    };

    try {
      setIsLoading(true);
      const response = await apiClient.post(
        // `https://j11c201.p.ssafy.io/api/moapay/core/generalpay/pay`,
        `/api/moapay/core/generalpay/pay`,
        // `http://localhost:8765/moapay/core/generalpay/pay`,
        {
          requestId: storedRequestId,
          orderId: storedOrderId,
          merchantId: storedMerchantId,
          categoryId: storedCategoryId,
          totalPrice: parseInt(storedTotalPrice, 10),
          memberId: id,
          cardSelectionType: "FIX",
          recommendType: paymentType || "PERFORM", // RECOMMEND인 경우 사용, BENEFIT / PERFORM
          cardNumber: cardList[0].cardNumber, // FIX인 경우 사용
          cvc: cardList[0].cvc, // FIX인 경우 사용
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
  //인증이 true면 진행 모달을 닫고
  useEffect(() => {
    const storedRequestId = localStorage.getItem("requestId");
    const storedOrderId = localStorage.getItem("orderId");
    const storedMerchantId = localStorage.getItem("merchantId");
    const storedCategoryId = localStorage.getItem("categoryId");
    const storedTotalPrice = localStorage.getItem("totalPrice") || "0";
    const storedQRCode = localStorage.getItem("QRCode");
    console.log(
      storedRequestId,
      storedOrderId,
      storedMerchantId,
      storedCategoryId,
      storedTotalPrice
    );
    //분할
    if (selectedPayType == "multi" && isAuth) {
      console.log("=======================multi payment gogo=================");
      multiPaying();
    } else if (selectedPayType == "single" && isAuth) {
      singlePaying();
    }
    //단일
  }, [isAuth]);

  // DotNav를 위한 변수들
  const [swiperInstance, setSwiperInstance] = useState<SwiperInstance>(); // 초기값을 undefined로 설정
  const [activeIndex, setActiveIndex] = useState(0); // 화면에 띄워지고 있는 결과 페이지를 지정하기 위한 변수

  const handleNavClick = (index: number) => {
    swiperInstance?.slideTo(index); // swiperInstance가 존재할 때만 slideTo 호출
  };

  return (
    <Wrapper>
      <SelectView>
        <Title>MoA PaY</Title>
        <div
          className="type-btn"
          onClick={() => {
            setSelectedPayType("single");
          }}>
          <label className="container">
            <input checked={selectedPayType === "single"} type="checkbox" />
            <div className="checkmark"></div>
          </label>
          <p>단일결제</p>
        </div>
        <div
          className="type-btn"
          onClick={() => {
            setSelectedPayType("multi");
          }}>
          <label className="container">
            <input checked={selectedPayType === "multi"} type="checkbox" />
            <div className="checkmark"></div>
          </label>
          <p>추천결제</p>
        </div>
        <div
          className="type-btn"
          onClick={() => {
            setSelectedPayType("dutch");
          }}>
          <label className="container">
            <input checked={selectedPayType === "dutch"} type="checkbox" />
            <div className="checkmark"></div>
          </label>
          <p>더치페이</p>
        </div>
        <Button
          onClick={() => {
            startPay();
          }}>
          결제하기
        </Button>
      </SelectView>
      {isLoading && (
        <Loading>
          <div className="container">
            <div className="left-side">
              <div
                className={isLoading && isEnd ? "card card-fadeout" : "card "}>
                <div className="card-line"></div>
                <div className="buttons"></div>
              </div>
              <div className="post">
                <div className="post-line"></div>
                <div className="screen">
                  <div className={isLoading && isEnd ? "dollar " : ""}>
                    {isLoading && isEnd ? "$" : ""}
                  </div>
                </div>
                <div className="numbers"></div>
                <div className="numbers-line2"></div>
                <div className="numbers-line3"></div>
              </div>
              {!isEnd && (
                <div className="text">
                  <span>결</span>
                  <span>제</span>
                  <span>를</span>
                  <span> </span>
                  <span>진</span>
                  <span>행</span>
                  <span>중</span>
                  <span> </span>
                  <span>입</span>
                  <span>니</span>
                  <span>다</span>
                  <span>.</span>
                  <span>.</span>
                  <span>.</span>
                </div>
              )}
            </div>
          </div>
        </Loading>
      )}
      {isEnd && !isLoading && (
        <>
          <Result>
            <ResultBox>
              <Swiper
                onSwiper={(swiper) => setSwiperInstance(swiper)} // Swiper 인스턴스 저장
                onSlideChange={(swiper) => setActiveIndex(swiper.activeIndex)} // 슬라이드 변경 시 인덱스 업데이트
                spaceBetween={50}
                slidesPerView={1}>
                {paymentResult?.paymentResultCardInfoList?.map(
                  (result, index) => (
                    <SwiperSlide key={index}>
                      <ResultBoxInner>
                        <div>{result.cardName}</div>
                        <CardImg>
                          <img
                            src={`/assets/image/longHeight/신용카드이미지/${result.imageUrl}.png`}
                            alt={result.cardName}
                          />
                        </CardImg>
                        <Content>
                          <Record>
                            <div>결제 금액</div>
                            <div> {result.actualAmount.toLocaleString()}원</div>
                          </Record>
                          <Record>
                            <div>받은 혜택 금액</div>
                            <div style={{ color: "#a959ff" }}>
                              {(
                                result.benefitDetail.discount +
                                result.benefitDetail.point +
                                result.benefitDetail.cashback
                              ).toLocaleString()}
                              원
                            </div>
                          </Record>
                          <Record>
                            <div>실적</div>
                            <div>
                              {result.usedAmount.toLocaleString()} /{" "}
                              {result.performance.toLocaleString()}원
                            </div>
                          </Record>

                          <Etc>
                            <div>
                              {new Date().getMonth() + 1}월동안 총{" "}
                              <span>
                                {result.benefitUsage.toLocaleString()}원
                              </span>
                              의
                            </div>
                            <div>혜택을 받았어요!</div>
                          </Etc>
                        </Content>
                      </ResultBoxInner>
                    </SwiperSlide>
                  )
                )}
              </Swiper>
              {/* 하단 네브바 (점) */}
            </ResultBox>
            <DotNav>
              {paymentResult?.paymentResultCardInfoList.map((_, index) => (
                <span
                  key={index}
                  onClick={() => handleNavClick(index)}
                  style={{
                    backgroundColor: activeIndex === index ? "purple" : "white",
                  }}></span>
              ))}
            </DotNav>
            <HomeBtn
              onClick={() => {
                navigate(PATH.HOME);
              }}>
              <div>홈으로</div>
            </HomeBtn>
          </Result>
        </>
      )}
      {showModal && <BiometricsAuthModal endAuth={endAuth} />}
    </Wrapper>
  );
};
export default SelectPaymentType;
