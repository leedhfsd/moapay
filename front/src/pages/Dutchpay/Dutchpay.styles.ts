import styled from "styled-components";

export const Wrapper = styled.div`
  position: relative;
  height: 100vh;
  // overflow: auto; // [추후 삭제 예정] 방 생성 확인용으로 추가함
  // width: 100vw;
`;

export const Top = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 13% 0;
  text-align: center;
  font-size: 30px;
  font-weight: 700;
`;

export const Title = styled.div`
  display: flex;
  align-items: center;
  margin: 0 auto;
`;

export const CopyIcon = styled.div`
  position: absolute;
  right: 22px;
`;

export const RequestUrl = styled.div`
  color: white;
  // text-align: center;
  margin: 0 auto;
`;

export const ShareUrl = styled.div`
  text-align: center;
  margin: 0 auto;
  font-weight: 700;
  overflow: auto;
  align-items: center;
  // text-overflow: ellipsis; // 일정 길이 이상 ...처리
  overflow-y: hidden;
  white-space: nowrap;

  // 스크롤바 숨기기
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display: none;
  }
`;

export const Main = styled.div`
  position: relative;
  width: 85%;
  height: 80%;
  border: 3px solid black;
  border-top-right-radius: 30px;
  border-top-left-radius: 30px;
  background-color: rgba(255, 255, 255, 0.65);
  margin: 0 auto;
  padding: 20px 25px;
  z-index: 1;
`;

export const LinkBox = styled.div`
  display: flex;
  align-items: center;
  background-color: #c5caff;
  width: 75%;
  border-radius: 7px;
  padding: 4px 5px;
  padding-left: 21.704px;
  padding-right: 21.704px;
  margin: 0 auto;
  height: 36px;
  font-size: 18px;
  font-weight: 100;

  & > svg {
    focus: cusor;
  }

  & > input {
    width: 100%;
    // height: 100%;
    font-size: 20px;
    text-align: center;
    background-color: rgba(0, 0, 0, 0);
    border: none;
  }
  :focus {
    outline: none; // 클릭시 테두리 안 생기도록
  }
  // placeholder 설정
  ::-webkit-input-placeholder {
    color: black;
    font-size: 19px;
  }
`;

export const BackImg = styled.div`
  // display: flex;
  z-index: -1;
  & > img {
    position: absolute;
  }

  & > img:nth-of-type(1) {
    bottom: -15px;
    left: -50%;
  }
  & > img:nth-of-type(2) {
    bottom: -17px;
    // left: -50%;
  }
  & > img:nth-of-type(3) {
    bottom: -15px;
    right: -50%;
  }
`;

// 더치페이 참가자 전환 화면
export const InviteTop = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 13% 0;
  height: 44%;
`;

export const InviteTitle = styled.div`
  display: flex;
  align-items: center;
  margin: 0 auto;
  text-align: center;
  font-size: 30px;
  font-weight: 700;
`;

export const Timer = styled.div`
  margin: 0 auto;
  font-size: 23px;
  color: #676767;
  padding-bottom: 10px;
`;

export const ProcessContainer = styled.div`
  display: flex;
  align-items: center;
  padding-left: 70px;
  position: relative;
  // height: 170px;
`;

export const ProcessLine = styled.div`
  border: 1px solid black;
  height: 160px;
  width: 0;
  position: absolute;
  left: 79px;
  z-index: -1;
`;

export const Process = styled.div`
  display: flex;
  flex-direction: column;
  gap: 19px;
  font-size: 17px;
`;

export const Step = styled.div`
  display: flex;
  align-items: center;
  gap: 20px;

  & > div:nth-of-type(1) {
    width: 20px;
    height: 20px;
    border: 2px solid black;
    border-radius: 100%;
    background-color: white;
  }
`;

export const JoinBtnDiv = styled.div`
  position: absolute;
  width: 85%;
  bottom: 20px;
  text-align: center;

  // absolute 요소 중앙 정렬
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
`;

export const InviteMain = styled.div`
  position: relative;
  width: 85%;
  height: 60%;
  border: 3px solid black;
  border-top-right-radius: 30px;
  border-top-left-radius: 30px;
  background-color: rgba(255, 255, 255, 0.65);
  margin: 0 auto;
  padding: 20px 25px;
  z-index: 1;
`;

export const DutchWaiting = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding-top: 70%;

  & > div > span {
    font-size: 29px;
    display: inline-block; /* 움직이려면 필요함 */
    animation: wave 1.5s infinite; /* 애니메이션 설정 */
  }

  & > div:nth-of-type(1) {
    display: flex;
    justify-content: center;
  }

  & > div > span:nth-of-type(1) {
    animation-delay: 0s;
  }
  & > div > span:nth-of-type(2) {
    animation-delay: 0.1s;
  }
  & > div > span:nth-of-type(3) {
    animation-delay: 0.18s;
  }
  & > div > span:nth-of-type(4) {
    animation-delay: 0.26s;
  }
  & > div > span:nth-of-type(5) {
    animation-delay: 0.34s;
  }
  & > div > span:nth-of-type(6) {
    animation-delay: 0.42s;
  }
  & > div > span:nth-of-type(7) {
    animation-delay: 0.48s;
  }
  & > div > span:nth-of-type(8) {
    animation-delay: 0.54s;
  }
  & > div > span:nth-of-type(9) {
    animation-delay: 0.6s;
  }

  & > div:nth-of-type(2) {
    color: #7b85ec;
    font-size: 15px;
  }

  /* 애니메이션 정의 */
  @keyframes wave {
    0% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-10px); /* 글자가 위로 올라감 */
    }
    100% {
      transform: translateY(0); /* 다시 원래 위치로 */
    }
  }
`;

export const DutchFin = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
`;

export const FinContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: absolute;
  // justify-content:center;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  font-size: 23px;

  & > div:nth-of-type(1) {
    width: 90px;
    height: 90px;
    border-radius: 100%;
    diaplay: flex;
    margin: auto;
    // align-items: center;
    background-color: #c5caff;
    position: relative;
  }

  & > div:nth-of-type(1) > svg {
    position: absolute;
    text-align: center;
    justify-content: center;
    align-items: center;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
`;

export const Bottom = styled.div`
  position: absolute;
  width: 100%;
  bottom: 10px;
  text-align: center;

  // absolute 요소 중앙 정렬
  left: 50%;
  transform: translate(-50%, -50%);
`;

export const Btn = styled.button`
  background-color: white;
  width: 78%;
  height: 60px;
  font-size: 19px;
  font-weight: 700;
  border: 2px solid black;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
`;

// process 4 화면을 위한 css
export const ParticipantTitle = styled.div`
  font-weight: 700;
  font-size: 24px;
  text-align: center;
`;

export const PartiList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-top: 15px;
`;

export const PartiInfo = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  & > input {
    width: 28%;
    text-align: center;
    background-color: rgba(197, 202, 255, 0.44);
    border: 0;
    border-radius: 5px;
    padding: 3px 0;
  }

  & > div:nth-of-type(2) {
    font-size: 20px;
  }
`;
