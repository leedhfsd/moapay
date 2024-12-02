import styled from "styled-components";

//보라색 바탕 부분
export const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(120deg, #f1e5ff 5%, #dcbefc 50%, #ffc6ff);
  padding: 0px 10% 100px 10%;
`;
// 흰색 바탕 부분
export const CheckoutWrapper = styled.div`
  position: relative;
  width: 100%;
  /* height: 100%; */
  background: #ffffff;
  border-radius: 15px;
  padding: 50px 30px 50px 30px;
`;

// 카드 부분
export const CreditCardBox = styled.div<{ isFlipped: boolean }>`
  margin-bottom: 30px;
  padding: 0px 15px 10px 10px;
  width: 100%;
  height: 150px;
  background: linear-gradient(135deg, #e3a8ff, #c473f6, #893eb6);
  background: linear-gradient(135deg, #e3a8ff, #c473f6);
  /* background: linear-gradient(120deg, #f1e5ff, #dcbefc, #ffc6ff); */
  /* background-color: var(--light-purple); */
  border-radius: 15px;
  transition: 0.4s;
  transform-style: preserve-3d;
  position: relative;
  transform: ${({ isFlipped }) =>
    isFlipped ? "rotateY(180deg)" : "rotateY(0deg)"}; // 상태에 따라 회전
`;

export const Flip = styled.div`
  padding: 10px 5px;
`;

// 카드 앞면
export const CardFront = styled.div<{ isFlipped: boolean }>`
  z-index: ${({ isFlipped }) => (isFlipped ? 1 : 2)};
  opacity: ${({ isFlipped }) => (isFlipped ? 0 : 1)};
  transform: rotateY(0deg);
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: opacity 0.4s ease-in-out, z-index 0.4s ease-in-out;

  .card-front-top {
    width: 100%;
    display: flex;
    justify-content: space-between;
    .chip {
      width: 60px;
      height: 55px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    .logo {
      height: 60px;
    }
  }
  .card-front-middle {
    color: #390b55;
    font-weight: bold;
    margin: 0 auto;
    font-size: 20px;
    height: 23px;
    margin-bottom: 10px;
  }
  .card-front-end {
    display: flex;
    flex-direction: column;
    width: 100%;
    align-items: flex-end;
    text-align: end;
    label {
      font-size: 12px;
    }
    div {
      margin-top: 3px;
      font-size: 15px;
      font-weight: bold;
    }
  }
`;

// 카드 뒷면
export const CardBack = styled.div<{ isFlipped: boolean }>`
  z-index: ${({ isFlipped }) => (isFlipped ? 2 : 1)};
  opacity: ${({ isFlipped }) => (isFlipped ? 1 : 0)};
  transform: rotateY(180deg);
  transition: opacity 0.4s ease-in-out, z-index 0.4s ease-in-out;

  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  .strip {
    width: 100%;
    background-color: black;
    height: 30px;
    margin-top: 20px;
  }
  .ccv {
    padding: 15px 0px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    label {
      display: block;
      padding-right: 20px;
      font-size: 13px;
    }
    div {
      margin: auto;
      padding: 5px 20px;
      width: 90%;
      background-color: white;
      height: 28px;
      border-radius: 5px;
    }
  }
  .logo {
    position: absolute;
    bottom: 0px;
    right: 10px;
    width: 100%;
    height: 40px;
    display: flex;
    justify-content: end;
    svg {
      height: 40px;
    }
  }
`;

export const CardLogo = styled.div`
  position: absolute;
  top: 9px;
  right: 20px;
  width: 60px;
  svg {
    width: 100%;
    height: auto;
    fill: #fff;
  }
`;

export const Input = styled.input`
  width: 100%;
  height: 38px;
  color: hsl(0, 0, 20);
  padding: 10px;
  border-radius: 5px;
  font-size: 15px;
  outline: none !important;
  border: 1px solid black;
`;

export const CardForm = styled.form`
  .card-number-view {
    display: flex;
    gap: 10px;
  }
  .label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
  }
  & > div {
    margin-bottom: 15px;
  }
  .second-input-box {
    display: flex;
    & > div {
      display: flex;
      flex-direction: column;
    }
    & > div:nth-child(1) {
      flex: 1;
      width: 100%;
      div {
        display: flex;
        gap: 5px;
      }
      div select {
        width: 35%;
        height: 38px;
        border-radius: 5px;
        border: 1px solid black;
      }
    }
    & > div:nth-child(2) {
      width: 30%;
    }
  }
`;

export const Button = styled.button`
  margin-top: 10px;
  width: 100%;
  font-size: 18px;
  padding: 15px 0px;
  border-radius: 10px;
  /* background-color: var(--light-purple); */
  background-color: #c473f6;
  /* background: linear-gradient(135deg, #e3a8ff, #c473f6); */
  color: white;
  font-weight: bold;
  border: none;
  outline: none !important;
`;

export const ModalView = styled.div`
  position: absolute;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  z-index: 100;
  background-color: rgba(1, 1, 1, 0.8);
  & > div {
    border-radius: 15px;
    padding: 0px 20px;
    width: 70%;
    height: 30%;
    background-color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    Button {
      background-color: #c473f6;
      border: none;
      padding: 10px;
      border-radius: 10px;
      width: 80%;
      margin-top: 30px;
      color: white;
    }
  }
`;
