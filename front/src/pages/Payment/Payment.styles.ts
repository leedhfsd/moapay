import styled from "styled-components";

export const Wrapper = styled.div`
  background-color: #B97DF9;
  height: 100vh;
  width: 100vw;
  position: relative;
`

export const PaymentWaiting = styled.div`
  // padding: 50% 0;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 20px;
  & > svg {
    margin: 0 auto;
  }

& > div {
    font-size: 22px;
    color: white;
    width: 300px;

    span {
      display: inline-block;
      opacity: 0; /* 초기 상태에서 보이지 않음 */
      animation: fadeIn 1.5s forwards infinite;
    }

    span:nth-of-type(1) {
      animation-delay: 0s;
    }
    span:nth-of-type(2) {
      animation-delay: 0.1s;
    }
    span:nth-of-type(3) {
      animation-delay: 0.2s;
    }
    span:nth-of-type(4) {
      animation-delay: 0.3s;
    }
    span:nth-of-type(5) {
      animation-delay: 0.4s;
    }
    span:nth-of-type(6) {
      animation-delay: 0.5s;
    }
    span:nth-of-type(7) {
      animation-delay: 0.6s;
    }
    span:nth-of-type(8) {
      animation-delay: 0.7s;
    }
    span:nth-of-type(9) {
      animation-delay: 0.8s;
    }
    span:nth-of-type(10) {
      animation-delay: 0.9s;
    }
    span:nth-of-type(11) {
      animation-delay: 1.0s;
    }
    span:nth-of-type(12) {
      animation-delay: 1.1s;
    }
    span:nth-of-type(13) {
      animation-delay: 1.2s;
    }
    span:nth-of-type(14) {
      animation-delay: 1.3s;
    }
  }

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

`

export const Result = styled.div`
  display: flex;
  flex-direction: column;
  gap: 30px;
  position: absolute;
  width: 100%;
  height: 100%;
  justify-content: center;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
`

export const ResultBox = styled.div`
  background-color: white;
  border: 3px solid black;
  padding: 30px 20px;
  border-radius: 25px;
  width: 85%;
  height: 70%;
  margin: 0 auto;
  overflow: auto;
`

export const ResultBoxInner = styled.div`
  display: flex;
  flex-direction: column;
  // gap: 20px;
  align-items: center;
  margin: 0 auto;
  font-size: 22px;
  text-align: center;
  // justify-content: center; // 내용 수직 중앙 정렬됨

  & > div:nth-of-type(1){
    font-size: 28px;
    font-weight: 700;
  }
`

export const CardImg = styled.div`

`

export const Content = styled.div`
  display: flex;
  flex-direction: column;
  gap: 35px;
  padding: 25px 0;
`

export const Record = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;

  & > div:nth-of-type(1){
    font-size: 25px;
    font-weight: 700;
  }
`

export const Etc = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #320088;
  font-size: 25px;
  font-weight: 700;
`

export const DotNav = styled.div`
  text-align: center;

  & > span{
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin: 0 5px;
    cursor: pointer;
  }
`

export const HomeBtn = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
  margin: 0 auto;
  color: white;
  font-size: 20px;
`