import styled, { keyframes } from "styled-components";
const slide = keyframes`
  0% {
    transform: translateX(0px);
  }
  25% {
    transform: translateX(-30px);
  }
  50% {
    transform: translateX(0px);
  }
  75% {
    transform: translateX(-30px);
  }
  100% {
    transform: translateX(0px);
  }
`;
// rotate 속성을 받아 회전 여부에 따라 다른 스타일을 적용할 수 있습니다.
export const CardInfo = styled.div`
  .row {
    border-bottom: 1px solid var(--light-gray);
    display: flex;
    width: 100%;
    height: 110px;
    align-items: center;
    transition: transform 0.3s ease; /* 애니메이션을 부드럽게 만듦 */
    div {
      width: 33%;
      position: relative;
      margin-right: 10px;
    }
    h3 {
      width: 60%;
    }
    img {
      object-fit: cover; // 이미지가 잘리지 않고 잘 맞도록 조정
      width: 100%;
      height: 100%;
    }
  }

  /**
  .active = CardInfo의 자식일 경우
  &.active = CardInfo 자신을 참조
  */
  .active {
    animation: ${slide} 3s ease-in-out;
  }
  .nofity {
    /* position: absolute; */
    /* right: 0; */
    /* width: fit-content; */
    /* top: -30px; */
    /* border-bottom: 3px solid #ddc1fc; */
    /* background-color: #ddc1fc; */
    /* padding: 10px 15px; */
    /* text-align: center; */
    /* border-radius: 10px; */
  }
`;
