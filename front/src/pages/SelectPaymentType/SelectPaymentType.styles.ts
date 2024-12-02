import styled, { keyframes } from "styled-components";

export const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  padding: 33% 10%;
`;
export const SelectView = styled.div`
  border-radius: 20px;
  /* height: 100%; */
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  padding: 40px 30px 30px 30px;
  background-color: rgba(255, 255, 255, 0.5);
  .type-btn {
    width: 100%;
    padding: 20px 20px;
    background-color: rgba(255, 255, 255, 0.7);
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    p {
      margin-left: 20px;
    }
    .container input {
      position: absolute;
      opacity: 0;
      cursor: pointer;
      height: 0;
      width: 0;
    }

    .container {
      display: block;
      position: relative;
      cursor: pointer;
      font-size: 1.5rem;
      user-select: none;
    }

    /* Create a custom checkbox */
    .checkmark {
      --clr: #a959ff;
      position: relative;
      top: 0;
      left: 0;
      height: 1.3em;
      width: 1.3em;
      background-color: #ccc;
      border-radius: 50%;
      transition: 300ms;
    }

    /* When the checkbox is checked, add a blue background */
    .container input:checked ~ .checkmark {
      background-color: var(--clr);
      border-radius: 50%;
      animation: pulse 500ms ease-in-out;
    }

    /* Create the checkmark/indicator (hidden when not checked) */
    .checkmark:after {
      content: "";
      position: absolute;
      display: none;
    }

    /* Show the checkmark when checked */
    .container input:checked ~ .checkmark:after {
      display: block;
    }

    /* Style the checkmark/indicator */
    .container .checkmark:after {
      left: 0.45em;
      top: 0.25em;
      width: 0.25em;
      height: 0.5em;
      border: solid #e0e0e2;
      border-width: 0 0.15em 0.15em 0;
      transform: rotate(45deg);
    }

    @keyframes pulse {
      0% {
        box-shadow: 0 0 0 #a959ff90;
        rotate: 20deg;
      }

      50% {
        rotate: -20deg;
      }

      75% {
        box-shadow: 0 0 0 10px #a959ff60;
      }

      100% {
        box-shadow: 0 0 0 13px #a959ff30;
        rotate: 0;
      }
    }
  }
`;
export const Title = styled.div`
  position: absolute;
  top: -20px;
  font-size: 40px;
  font-weight: 800;
`;
export const Button = styled.div`
  width: 100%;
  text-align: center;
  padding: 20px 0px;
  background-color: #dcbefc;
`;

///////////////////////////Loading////////////////////////
// translateX 애니메이션 정의
const slideIn = keyframes`
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(-10%);
  }
`;

export const Loading = styled.div`
  position: absolute;
  height: 100vh;
  width: 100%;
  transform: translateX(-100%);
  animation: ${slideIn} 1s ease-in-out forwards;

  .container {
    display: flex;
    width: 100%;
    height: 100%;
    position: relative;
    border-radius: 6px;
    transition: 0.3s ease-in-out;
  }

  // 애니메이션을 담을 공간
  .left-side {
    /* background-color: #5de2a3; */
    background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
    width: 100%;
    height: 100%;
    border-radius: 4px;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: 0.3s;
    flex-shrink: 0;
    overflow: hidden;
  }

  @keyframes slide-down {
    0% {
      -webkit-transform: translateY(-200px) rotate(90deg);
      transform: translateY(-200px) rotate(90deg);
    }

    100% {
      -webkit-transform: translateY(-80px) rotate(90deg);
      transform: translateY(-80px) rotate(90deg);
      /* -webkit-transform: translateY(-8px) rotate(90deg);
      transform: translateY(-8px) rotate(90deg); */
    }
  }

  @keyframes slide-up {
    0% {
      -webkit-transform: translateY(-80px) rotate(90deg);
      transform: translateY(-80px) rotate(90deg);
    }

    100% {
      -webkit-transform: translateY(-200px) rotate(90deg);
      transform: translateY(-200px) rotate(90deg);
      /* -webkit-transform: translateY(-8px) rotate(90deg);
      transform: translateY(-8px) rotate(90deg); */
    }
  }
  //카드
  .card {
    width: 140px;
    height: 92px;
    background-color: #c289ff;
    border-radius: 12px;
    position: absolute;
    display: flex;
    z-index: 10;
    flex-direction: column;
    align-items: center;
    -webkit-box-shadow: 9px 9px 9px -2px rgba(135, 58, 224, 0.52);
    -moz-box-shadow: 9px 9px 9px -2px rgba(135, 58, 224, 0.52);
    -webkit-box-shadow: 9px 9px 9px -2px rgba(135, 58, 224, 0.52);
    transform: rotate(90deg);
    animation: slide-down 1.2s 0.8s cubic-bezier(0.645, 0.045, 0.355, 1) both;
  }
  .card-fadeout {
    animation: slide-up 1.2s cubic-bezier(0.645, 0.045, 0.355, 1) both;
  }

  .card-line {
    width: 130px;
    height: 26px;
    background-color: #f1e5ff;
    border-radius: 4px;
    margin-top: 14px;
  }

  .buttons {
    width: 16px;
    height: 16px;
    background-color: #873ae0;
    box-shadow: 0 -20px 0 0 #ae68ff, 0 20px 0 0 #9949f4;
    border-radius: 50%;
    margin-top: 5px;
    transform: rotate(90deg);
    margin: 10px 0 0 -60px;
  }

  .post {
    width: 126px;
    height: 200px;
    background-color: #dddde0;
    position: absolute;
    z-index: 11;
    border-radius: 6px;
    overflow: hidden;
    -webkit-box-shadow: 9px 9px 9px -2px rgba(155, 155, 155, 0.5);
    -moz-box-shadow: 9px 9px 9px -2px rgba(155, 155, 155, 0.5);
    -webkit-box-shadow: 9px 9px 9px -2px rgba(155, 155, 155, 0.5);
  }

  .post-line {
    width: 94px;
    height: 18px;
    background-color: #545354;
    position: absolute;
    border-radius: 0px 0px 6px 6px;
    right: 16px;
    top: 8px;
  }

  .post-line:before {
    content: "";
    position: absolute;
    width: 94px;
    height: 18px;
    background-color: #757375;
    top: -16px;
  }

  .screen {
    width: 94px;
    height: 46px;
    background-color: #ffffff;
    position: absolute;
    top: 44px;
    right: 16px;
    border-radius: 3px;
  }

  .numbers {
    width: 24px;
    height: 24px;
    background-color: #838183;
    box-shadow: 0 -36px 0 0 #838183, 0 36px 0 0 #838183;
    border-radius: 2px;
    position: absolute;
    transform: rotate(90deg);
    left: 50px;
    top: 104px;
  }

  .numbers-line2 {
    width: 24px;
    height: 24px;
    background-color: #aaa9ab;
    box-shadow: 0 -36px 0 0 #aaa9ab, 0 36px 0 0 #aaa9ab;
    border-radius: 2px;
    position: absolute;
    transform: rotate(90deg);
    left: 50px;
    top: 136px;
  }
  .numbers-line3 {
    width: 24px;
    height: 24px;
    background-color: #aaa9ab;
    box-shadow: 0 -36px 0 0 #aaa9ab, 0 36px 0 0 #aaa9ab;
    border-radius: 2px;
    position: absolute;
    transform: rotate(90deg);
    left: 50px;
    top: 168px;
  }

  .dollar {
    position: absolute;
    font-size: 26px;
    font-family: "Lexend Deca", sans-serif;
    width: 100%;
    left: 0;
    top: 0;
    color: #4b953b;
    text-align: center;
    animation: fade-in-fwd 1s backwards;
  }
  @keyframes fade-in-fwd {
    0% {
      opacity: 0;
      transform: translateY(-10px);
    }

    100% {
      opacity: 1;
      transform: translateY(0);
    }
  }
  .text {
    position: absolute;
    font-size: 20px;
    top: 70%;

    @keyframes fadeIn {
      0% {
        opacity: 0;
      }
      50% {
        opacity: 1;
      }
      100% {
        opacity: 0; /* 애니메이션이 끝난 후 다시 사라짐 */
      }
    }
    span {
      display: inline-block;
      opacity: 0; /* 초기 상태에서 보이지 않음 */
      animation: fadeIn 3s forwards infinite;
    }

    span:nth-of-type(1) {
      animation-delay: 0s;
    }
    span:nth-of-type(2) {
      animation-delay: 0.2s;
    }
    span:nth-of-type(3) {
      animation-delay: 0.4s;
    }
    span:nth-of-type(4) {
      animation-delay: 0.6s;
    }
    span:nth-of-type(5) {
      animation-delay: 0.8s;
    }
    span:nth-of-type(6) {
      animation-delay: 1s;
    }
    span:nth-of-type(7) {
      animation-delay: 1.2s;
    }
    span:nth-of-type(8) {
      animation-delay: 1.4s;
    }
    span:nth-of-type(9) {
      animation-delay: 1.6s;
    }
    span:nth-of-type(10) {
      animation-delay: 1.8s;
    }
    span:nth-of-type(11) {
      animation-delay: 2s;
    }
    span:nth-of-type(12) {
      animation-delay: 2.2s;
    }
    span:nth-of-type(13) {
      animation-delay: 2.4s;
    }
    span:nth-of-type(14) {
      animation-delay: 2.6s;
    }
  }
`;

const slideUp = keyframes`
  0% {
    transform: translateY(100%);
  }
  100% {
    transform: translateX(0%);
  }
`;

export const Result = styled.div`
  position: absolute;
  height: 100vh;
  width: 100%;
  left: 0;
  transform: translateY(-100%);
  animation: ${slideUp} 0.5s forwards;
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const ResultBox = styled.div`
  background-color: rgba(255, 255, 255, 0.5);
  padding: 40px 20px 10px 20px;
  border-radius: 25px;
  width: 85%;
  height: 80%;
  margin: 0 auto;
  overflow: auto;
  -ms-overflow-style: none; /* IE와 Edge */
  scrollbar-width: none; /* Firefox */
  &::-webkit-scrollbar {
    display: none; /* 크롬, 사파리, 새로운 엣지 */
  }
`;

export const ResultBoxInner = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  // gap: 20px;
  align-items: center;
  margin: 0 auto;
  font-size: 22px;
  text-align: center;
  // justify-content: center; // 내용 수직 중앙 정렬됨

  & > div:nth-of-type(1) {
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
`;

export const CardImg = styled.div`
  height: 250px;
  img {
    width: 100%;
    height: 100%;
  }
`;

export const Content = styled.div`
  display: flex;
  flex-direction: column;
  gap: 35px;
  padding: 25px 0;
`;

export const Record = styled.div`
  display: flex;
  gap: 10px;

  & > div:nth-of-type(1) {
    font-weight: 700;
  }
`;

export const Etc = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 25px;
  font-weight: 700;
  span {
    color: #a959ff;
  }
`;

export const DotNav = styled.div`
  text-align: center;
  margin-top: 20px;
  & > span {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin: 0 5px;
    cursor: pointer;
  }
`;

export const HomeBtn = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
  margin: 0 auto;
  /* color: white; */
  font-size: 20px;
  margin-top: 20px;
`;
