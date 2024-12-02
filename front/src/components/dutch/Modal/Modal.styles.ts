import styled from "styled-components";

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5); /* 배경 어둡게 처리 */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000; /* 다른 요소들 위에 표시되도록 z-index 설정 */
`;

export const ModalContent = styled.div`
  background: white;
  padding: 20px;
  border-radius: 17px;
  border: none;
  max-width: 500px;
  width: 80%;
  min-height: 190px;
  position: relative;
  z-index: 1001; /* 모달 내용이 오버레이 위에 오도록 설정 */

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 35px;
  font-size: 17px;
  font-weight: 700;

  & > svg {
    position: absolute;
    right: 13px;
    top: 10px;
  }

  & > div:nth-of-type(1) {
    padding-top: 20%;
    text-align: center;
  }

  & > div:nth-of-type(2) {
    display: flex;
    gap: 40px;
  }

  & > div > button {
    border-radius: 50px;
    border: none;
    padding: 5px 10px;
    width: 80px;
    font-size: 15px;
    font-weight: 700;
  }
  & > div > button:nth-of-type(1) {
    /* background-color: #868686; */
    /* color: white; */
    background-color: white;
  }
  & > div > button:nth-of-type(2) {
    background-color: white;
    color: #625f68;
  }
`;
