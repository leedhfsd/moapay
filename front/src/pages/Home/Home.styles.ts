import styled from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import MediaQuery from "../../constants/styles";

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: 100vh;
  // background-color: #db80e5;
  background-color: #B97DF9;
`;
export const Top = styled.div`
  padding: 20px 41px;
  padding-top: 35px;
  & button {
    border: none;
    background-color: white;
  }
  ${MediaQuery.small} {
    & {
      padding: 20px 41px 15px 41px;
    }
  }
  ${MediaQuery.medium} {
    & {
      padding: 30px 41px 15px 41px;
    }
  }
  @media screen and (min-height: 840px) {
    & {
      padding: 10% 41px 8% 41px;
    }
  }
`;

export const BarcordArea = styled.div`
  margin-bottom: 19px;
  padding: 18px 30px 10px 30px;
  background-color: white;
  border-radius: 19px;

    // border: 2px solid black;
  ${MediaQuery.small} {
    padding: 10px 20px 10px 20px;
  }
  @media screen and (min-height: 840px) {
    padding: 18px 20px 10px 20px;
  }
`;
export const BarcordView = styled.div`
  svg {
    width: 100%;
    height: 80px;
  }
  @media screen and (min-height: 840px) {
    img {
      height: 100px;
    }
  }
`;
export const Time = styled.div`
  padding-top: 5px;
  display: flex;
  justify-content: center;
  /* justify-content: center; */
  gap: 5px;
`;
export const ButtonArea = styled.div`
  display: flex;
  justify-content: space-between;
  
  // border: 2px solid black;
  // border-radius: 10px;

  button {
    width: 100%;
    font-weight: 700;
    border-radius: 10px;
    padding: 10px 15px;
  }
  ${MediaQuery.small} {
    button {
      padding: 8px 15px;
      font-size: 12px;
    }
  }

  ${MediaQuery.medium} {
    button {
      padding: 8px 15px;
      font-size: 12px;
    }
  }
`;

export const Bottom = styled.div`
  position: relative;
  padding: 25px 40px var(--padding-bottom) 40px;
  /* height:100%; */
  overflow: hidden;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  flex: 1;
  background-color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  .edit-card {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 10px;
    color: #6c6c6c;
    margin-bottom: 10px;
  }
  .remaining-performance {
    width: 80%;
    text-align: center;
    font-size: 18px;
    font-weight: 400;
    padding: 5px 31px;
    background-color: rgba(214, 125, 249, 0.62);
    border-radius: 21px;
    margin: 12px 0px 45px 0px;
  }
  .tri {
    position: absolute;
    bottom: 85.5px;
    width: 0;
    height: 0;
    border-bottom: 100px solid rgba(84, 98, 255, 0.24);
    border-top: 100px solid transparent;
    border-left: 100px solid transparent;
    border-right: 100px solid transparent;
  }
  .tri-left {
    left: -100px;
  }
  .tri-right {
    right: -100px;
  }

  ${MediaQuery.small} {
    & {
      padding-top: 20px;
    }
    .remaining-performance {
      width: 80%;
      text-align: center;
      font-size: 14px;
      font-weight: 400;
      padding: 8px 31px;
      background-color: rgba(214, 125, 249, 0.62);
      border-radius: 21px;
      margin: 10px 0px 15px 0px;
    }
    .tri {
      position: absolute;
      bottom: 55.5px;
      width: 0;
      height: 0;
      border-bottom: 100px solid rgba(84, 98, 255, 0.24);
      border-top: 100px solid transparent;
      border-left: 100px solid transparent;
      border-right: 100px solid transparent;
    }
    .tri-left {
      left: -100px;
    }
    .tri-right {
      right: -100px;
    }
  }
  ${MediaQuery.medium} {
    & {
      padding-top: 25px;
    }
    .remaining-performance {
      width: 90%;
      text-align: center;
      font-size: 16px;
      font-weight: 400;
      padding: 8px 31px;
      background-color: rgba(214, 125, 249, 0.62);
      border-radius: 21px;
      margin: 10px 0px 35px 0px;
    }
    .tri {
      position: absolute;
      bottom: 55.5px;
      width: 0;
      height: 0;
      border-bottom: 100px solid rgba(84, 98, 255, 0.24);
      border-top: 100px solid transparent;
      border-left: 100px solid transparent;
      border-right: 100px solid transparent;
    }
    .tri-left {
      left: -100px;
    }
    .tri-right {
      right: -100px;
    }
  }
`;

export const CardList = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  perspective: 100px;
  transform-style: preserve-3d;
  flex: 1;
  width: 100%;
  /* overflow-y: scroll; */
  .container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    overflow: hidden;
    width: 100%;
  }
  .card {
    width: 100%; /* 원하는 너비로 설정 */
    aspect-ratio: 1.58 / 1; /* 1.58:1 비율 유지 */

    transition: 0.5s;
    position: absolute;
    & > img {
      width: 100%;
      height: 100%;
    }
  }
  .card:nth-of-type(1) {
    transform: translateY(-23%) translateZ(-0.2px);
    width: 85%;
  }
  .card:nth-of-type(2) {
    width: 100%;
    z-index: 10;
  }
  .card:nth-of-type(3) {
    width: 85%;
    transform: translateY(23%) translateZ(-0.2px);
  }
  ${MediaQuery.small} {
    .card:nth-of-type(1) {
      transform: translateY(-23%) translateZ(-0.2px);
      width: 85%;
    }
    .card:nth-of-type(3) {
      width: 85%;
      transform: translateY(23%) translateZ(-0.2px);
    }
  }
  ${MediaQuery.medium} {
    .card:nth-of-type(1) {
      transform: translateY(-28%) translateZ(-0.2px);
      width: 85%;
    }
    .card:nth-of-type(3) {
      width: 85%;
      transform: translateY(28%) translateZ(-0.2px);
    }
  }
  @media screen and (min-height: 800px) {
    .card:nth-of-type(1) {
      transform: translateY(-35%) translateZ(-0.2px);
      width: 85%;
    }
    .card:nth-of-type(3) {
      width: 85%;
      transform: translateY(35%) translateZ(-0.2px);
    }
  }
  .card:nth-of-type(n + 4) {
    display: none;
  }

  .add-card {
    background-color: white;
    color: rgba(125, 136, 255, 0.86);
    border: 3px solid rgba(125, 136, 255, 0.86);
    border-radius: 10px;
    display: flex;
    justify-content: center;
    padding: 25px 0px 0px 0px;
    font-size: 20px;
    p {
      margin-top: 5px;
    }
  }
  /* 두 번째 .add-card에 스타일 적용 */
  .card:nth-of-type(2).add-card {
    font-size: 25px;
    padding: 0px;
    display: flex;
    justify-content: center;
    align-items: center;
    p {
      margin-top: 0px;
    }
  }
`;
export const PlusIcon = styled(FontAwesomeIcon)`
  height: 13px;
  width: 13px;
  border: 2px solid rgba(125, 136, 255, 0.86);
  margin-right: 5px;
  border-radius: 50%;
  padding: 5px 5px;
`;

export const QrContainer = styled.div`
  width: 100%;

  // border: 2px solid yellow;
  position: relative;
  padding: 0px;
  border: 0 !important;

  & > div:nth-of-type(1) > img {
    display: none;
  }

  #qr-reader__scan_region {
    min-height: 90px !important;
  }

  #qr-reader__dashboard_section_csr > div > button {
    padding: 7px 10px;
  }

  #qr-reader__dashboard_section > div > span {
    display: inline-block;
    padding-top: 9px !important;
    font-size: 17px;
    font-weight: 200;
    color: gray;
  }

  #qr-reader__dashboard_section > div > div:nth-of-type(2) {
    border: none !important;
    margin-bottom: 0 !important;
    padding-bottom: 0 !important;
  }
  #qr-reader__dashboard_section > div > div:nth-of-type(2) > div {
    display: none;
  }

  & button {
    background-color: #e5affb;
    border-radius: 10px;
    padding: 7px 10px;
  }
`;
