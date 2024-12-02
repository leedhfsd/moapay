import React, { useState } from "react";
import {
  Button,
  CardBack,
  CardFront,
  CheckoutWrapper,
  CreditCardBox,
  Flip,
  Input,
  Wrapper,
  CardForm,
  ModalView,
} from "./RegisterCard.styles";
import axios from "axios";
import { useAuthStore } from "../../store/AuthStore";
import { useCardStore } from "../../store/CardStore";
import apiClient from "../../axios";

const RegisterCard: React.FC = () => {
  // const baseUrl = import.meta.env.VITE_BASE_URL;
  const baseUrl = `http://localhost:8765/`;
  const { id, accessToken } = useAuthStore();
  const { addCard } = useCardStore();
  const [cardNumber, setCardNumber] = useState(["", "", "", ""]);
  const [expirationMonth, setExpirationMonth] = useState("");
  const [expirationYear, setExpirationYear] = useState("");
  const [cvc, setCvc] = useState("");
  const [isFlipped, setIsFlipped] = useState(false); // 카드 뒤집기 상태
  const [showModal, setShowModal] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);
  const [modalMessage, setModalMessage] =
    useState("카드 등록이 완료되었습니다!"); // 모달 메시지 상태

  const handleCardNumberChange = (
    e: React.ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    const value = e.target.value;
    setCardNumber((prev) => {
      const newCardNumber = [...prev];
      newCardNumber[index] = value;
      return newCardNumber;
    });
  };

  // 월 선택 시 핸들러
  const handleMonthChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setExpirationMonth(e.target.value);
  };

  // 년도 선택 시 핸들러 (마지막 두 자리만 저장)
  const handleYearChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const fullYear = e.target.value;
    setExpirationYear(fullYear.slice(2)); // 마지막 두 자리를 저장
  };

  const handleFlip = () => {
    setIsFlipped((prev) => !prev); // 상태를 반전시켜 카드를 뒤집음
  };

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 10 }, (_, i) => currentYear + i);

  const RegisterCard = async () => {
    setShowModal(true);
    setIsLoading(true);
    setError(false); // 에러 초기화d
    console.log(cardNumber.join(""));
    try {
      const response = await apiClient.post(
        // `${baseUrl}moapay/core/card/registration`,
        // `https://j11c201.p.ssafy.io/api/moapay/core/card/registration`,
        `/api/moapay/core/card/registration`,
        {
          memberUuid: id,
          cardNumber: cardNumber.join(""), // 카드 번호를 배열이 아닌 문자열로 전송
        },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );

      setTimeout(() => {
        setIsLoading(false);
      }, 3000);
      if (response.data.status === "CREATED") {
        // 카드 등록 성공 시 처리
        console.log(response.data.data);
        addCard(response.data.data);
        setModalMessage("카드 등록이 <br/>완료되었습니다!");
      } else {
        // 등록 실패 시 에러 처리
        setError(true);
        setModalMessage("카드 등록 중 <br/>오류가 발생했습니다.");
      }
    } catch (e) {
      setIsLoading(false);
      setError(true);
      setModalMessage("서버 요청 중 <br/>오류가 발생했습니다.");
      console.log(e);
    }
  };

  const closeModal = () => {
    setShowModal(false);
    if (!error) {
      // 성공적인 등록일 때 홈으로 이동 로직 추가
      window.location.href = "/home"; // 홈으로 리다이렉트 (홈 경로를 원하는 대로 수정)
    }
  };

  return (
    <Wrapper>
      <CheckoutWrapper>
        <CreditCardBox onClick={handleFlip} isFlipped={isFlipped}>
          <Flip>
            <CardFront isFlipped={isFlipped}>
              <div>
                <div className="card-front-top">
                  <div className="chip">
                    <img src="/assets/image/chip2.png" />
                  </div>
                  <div className="logo">
                    <svg
                      version="1.1"
                      id="visa"
                      xmlns="http://www.w3.org/2000/svg"
                      xmlnsXlink="http://www.w3.org/1999/xlink"
                      x="0px"
                      y="0px"
                      width="47.834px"
                      height="47.834px"
                      viewBox="0 0 47.834 47.834"
                    >
                      <g>
                        <g>
                          <path
                            d="M44.688,16.814h-3.004c-0.933,0-1.627,0.254-2.037,1.184l-5.773,13.074h4.083c0,0,0.666-1.758,0.817-2.143
                     c0.447,0,4.414,0.006,4.979,0.006c0.116,0.498,0.474,2.137,0.474,2.137h3.607L44.688,16.814z M39.893,26.01
                     c0.32-0.819,1.549-3.987,1.549-3.987c-0.021,0.039,0.317-0.825,0.518-1.362l0.262,1.23c0,0,0.745,3.406,0.901,4.119H39.893z
                     M34.146,26.404c-0.028,2.963-2.684,4.875-6.771,4.875c-1.743-0.018-3.422-0.361-4.332-0.76l0.547-3.193l0.501,0.228
                     c1.277,0.532,2.104,0.747,3.661,0.747c1.117,0,2.313-0.438,2.325-1.393c0.007-0.625-0.501-1.07-2.016-1.77
                     c-1.476-0.683-3.43-1.827-3.405-3.876c0.021-2.773,2.729-4.708,6.571-4.708c1.506,0,2.713,0.31,3.483,0.599l-0.526,3.092
                     l-0.351-0.165c-0.716-0.288-1.638-0.566-2.91-0.546c-1.522,0-2.228,0.634-2.228,1.227c-0.008,0.668,0.824,1.108,2.184,1.77
                     C33.126,23.546,34.163,24.783,34.146,26.404z M0,16.962l0.05-0.286h6.028c0.813,0.031,1.468,0.29,1.694,1.159l1.311,6.304
                     C7.795,20.842,4.691,18.099,0,16.962z M17.581,16.812l-6.123,14.239l-4.114,0.007L3.862,19.161
                     c2.503,1.602,4.635,4.144,5.386,5.914l0.406,1.469l3.808-9.729L17.581,16.812L17.581,16.812z M19.153,16.8h3.89L20.61,31.066
                     h-3.888L19.153,16.8z"
                          />
                        </g>
                      </g>
                    </svg>
                  </div>
                </div>
              </div>
              <div className="number card-front-middle">
                {cardNumber.join(" ")}
              </div>
              <div className="card-front-end card-expiration-date">
                <label>Expires</label>
                <div>
                  {expirationMonth && `${expirationMonth}/`}
                  {expirationYear}
                </div>
              </div>
            </CardFront>
            <CardBack isFlipped={isFlipped}>
              <div className="strip"></div>
              <div className="logo">
                <svg
                  version="1.1"
                  id="visa"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlnsXlink="http://www.w3.org/1999/xlink"
                  x="0px"
                  y="0px"
                  width="47.834px"
                  height="47.834px"
                  viewBox="0 0 47.834 47.834"
                >
                  <g>
                    <g>
                      <path
                        d="M44.688,16.814h-3.004c-0.933,0-1.627,0.254-2.037,1.184l-5.773,13.074h4.083c0,0,0.666-1.758,0.817-2.143
                     c0.447,0,4.414,0.006,4.979,0.006c0.116,0.498,0.474,2.137,0.474,2.137h3.607L44.688,16.814z M39.893,26.01
                     c0.32-0.819,1.549-3.987,1.549-3.987c-0.021,0.039,0.317-0.825,0.518-1.362l0.262,1.23c0,0,0.745,3.406,0.901,4.119H39.893z
                     M34.146,26.404c-0.028,2.963-2.684,4.875-6.771,4.875c-1.743-0.018-3.422-0.361-4.332-0.76l0.547-3.193l0.501,0.228
                     c1.277,0.532,2.104,0.747,3.661,0.747c1.117,0,2.313-0.438,2.325-1.393c0.007-0.625-0.501-1.07-2.016-1.77
                     c-1.476-0.683-3.43-1.827-3.405-3.876c0.021-2.773,2.729-4.708,6.571-4.708c1.506,0,2.713,0.31,3.483,0.599l-0.526,3.092
                     l-0.351-0.165c-0.716-0.288-1.638-0.566-2.91-0.546c-1.522,0-2.228,0.634-2.228,1.227c-0.008,0.668,0.824,1.108,2.184,1.77
                     C33.126,23.546,34.163,24.783,34.146,26.404z M0,16.962l0.05-0.286h6.028c0.813,0.031,1.468,0.29,1.694,1.159l1.311,6.304
                     C7.795,20.842,4.691,18.099,0,16.962z M17.581,16.812l-6.123,14.239l-4.114,0.007L3.862,19.161
                     c2.503,1.602,4.635,4.144,5.386,5.914l0.406,1.469l3.808-9.729L17.581,16.812L17.581,16.812z M19.153,16.8h3.89L20.61,31.066
                     h-3.888L19.153,16.8z"
                      />
                    </g>
                  </g>
                </svg>
              </div>
              <div className="ccv">
                <label>CVC</label>
                <div>{cvc}</div>
              </div>
            </CardBack>
          </Flip>
        </CreditCardBox>
        <CardForm autoComplete="off" noValidate>
          <div className="first-input-box">
            <label className="label" htmlFor="card-number">
              Card Number
            </label>
            <div className="card-number">
              <div className="card-number-view">
                {cardNumber.map((num, index) => (
                  <Input
                    key={index}
                    type="text"
                    maxLength={4}
                    value={num}
                    onChange={(e) => handleCardNumberChange(e, index)}
                  />
                ))}
              </div>
            </div>
          </div>
          <div className="second-input-box">
            <div>
              <label className="label" htmlFor="card-expiration-month">
                Expiration date
              </label>
              <div>
                <select
                  id="card-expiration-month"
                  value={expirationMonth}
                  onChange={handleMonthChange}
                >
                  <option value="" disabled>
                    MM
                  </option>
                  <option>01</option>
                  <option>02</option>
                  <option>03</option>
                  <option>04</option>
                  <option>05</option>
                  <option>06</option>
                  <option>07</option>
                  <option>08</option>
                  <option>09</option>
                  <option>10</option>
                  <option>11</option>
                  <option>12</option>
                </select>
                <select
                  id="card-expiration-year"
                  value={expirationYear}
                  onChange={handleYearChange}
                >
                  <option value="" disabled>
                    YY
                  </option>
                  {years.map((year) => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div>
              <label className="label" htmlFor="card-ccv">
                CVC
              </label>
              <Input
                type="text"
                id="card-ccv"
                maxLength={3}
                value={cvc}
                onChange={(e) => setCvc(e.target.value)}
              />
            </div>
          </div>

          <Button
            type="button" // 버튼 타입을 명시적으로 지정
            onClick={() => {
              RegisterCard();
            }}
          >
            등록하기
          </Button>
        </CardForm>
      </CheckoutWrapper>
      {showModal && (
        <ModalView>
          <div>
            {isLoading ? (
              <>
                <p>카드 등록 중...</p>
              </>
            ) : (
              <>
                <div
                  style={{ textAlign: "center", lineHeight: "25px" }}
                  dangerouslySetInnerHTML={{ __html: modalMessage }}
                />

                <button onClick={closeModal}>확인</button>
              </>
            )}
          </div>
        </ModalView>
      )}
    </Wrapper>
  );
};

export default RegisterCard;
