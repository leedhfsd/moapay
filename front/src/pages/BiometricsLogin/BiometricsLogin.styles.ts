import styled, { keyframes } from "styled-components";

const fadeOutAndIn = keyframes`
  0% {
    opacity: 1;
  }
  25%{
    opacity: 0;
  }
  50%{
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
`;

const fadeInAndOut = keyframes`
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  80% {
    opacity: 0;
  }
  100%{
    opacity: 0;
  }
`;

export const Wrapper = styled.div`
  /* background-color: var(--light-purple); */
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  padding: 30% 0% 30% 0%;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  .area {
    /* background-color: white; */
    background-color: rgba(255, 255, 255, 0.85);
    border-top-right-radius: 50px;
    border-top-left-radius: 50px;
    border-radius: 50px;
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 80%;
    padding: 70px 10px 0px 10px;
  }
`;
export const Header = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  h1 {
    text-align: center;
    font-size: 30px;
    font-weight: 800;
    margin-bottom: 20px;
  }
  p {
    font-size: 19px;
    margin-bottom: 30px;
  }
  div {
    position: relative;
    width: 90px;
    height: 90px;
    & > svg,
    & > img {
      position: absolute;
      width: 100%;
      height: 100%;
    }
    svg {
      animation: ${fadeOutAndIn} 4s infinite;
    }
    img {
      animation: ${fadeInAndOut} 4s infinite;
    }
  }
`;
export const Button = styled.button`
  width: 80%;
  height: 50px;
  background-color: #c473f6;
  background-color: #dec1fd;
  /* background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff); */
  border: none;
  border-radius: 10px;
  margin-top: 30px;
  font-size: 20px;
  color: white;
  color: black;
`;

export const Modal = styled.div`
  position: absolute;
  top: 0;
  background-color: rgba(1, 1, 1, 0.5);
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  .box {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 70%;
    height: 30%;
    background-color: white;
    border-radius: 20px;
  }
  .ment {
    width: 80%;
    text-align: center;
    font-size: 20px;
    margin-bottom: 10%;
  }
  button {
    width: 80%;
    height: 50px;
    border: none;
    font-size: 20px;
    border-radius: 20px;
    color: white;
    background-color: var(--light-purple);
  }
`;
const drawCheck = keyframes`
  0% {
    stroke-dashoffset: 100;
  }
  100% {
    stroke-dashoffset: 0;
  }
`;

export const CheckMarkContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100px;
  height: 100px;
`;

export const StyledSvg = styled.svg`
  width: 100%;
  height: 100%;

  path {
    fill: none;
    stroke: var(--light-purple);
    stroke-width: 5;
    stroke-linecap: round;
    stroke-dasharray: 100;
    stroke-dashoffset: 100;
    animation: ${drawCheck} 2s ease forwards;
  }
`;
