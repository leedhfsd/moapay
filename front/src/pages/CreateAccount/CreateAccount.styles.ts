import styled from "styled-components";
import MediaQuery from "../../constants/styles";
export const Wrapper = styled.div``;
export const MessageAuthenticationForm = styled.div`
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
  &.fade-in {
    animation: fadeIn 1.2s ease forwards;
  }
  padding: 25% 40px 25% 40px;
  ${MediaQuery.small} {
    padding: 20% 50px 20% 50px;
    .error {
      font-size: 11px;
    }
  }
`;
export const Header = styled.div`
  ${MediaQuery.small} {
    font-size: 20px;
    line-height: 30px;
    margin-bottom: 15px;
  }
  font-size: 23px;
  line-height: 33px;
  margin-bottom: 20px;
`;
export const BoldText = styled.span`
  font-weight: bold;
`;
export const Form = styled.div`
  width: 100%;
  .form-row {
    width: 100%;
    height: 50px;
    margin-bottom: 15px;
    input,
    select {
      font-size: 15px;
      padding-left: 15px;
      width: 100%;
      height: 100%;
      border-radius: 10px;
      border: 1px solid var(--light-gray);
    }
  }
  //생년월일 파트
  .birth {
    display: flex;
    //주민번호 앞자리와 뒷자리 파트가 40% 60%의 크기를 각자 가져감
    & > *:nth-child(1) {
      width: 40%;
    }
    & > *:nth-child(2) {
      width: 60%;
    }
    //주민번호 뒷자리 파트
    & > div:nth-child(2) {
      display: flex;
      input {
        width: 35px;
        padding-left: 3px;
        text-align: center;
      }
      .masking-part {
        display: flex;
        align-items: center;
        & > div {
          border-radius: 50%;
          height: 17px;
          width: 17px;
          margin-left: 5px;
          background-color: var(--light-gray);
        }
      }
      .line {
        background-color: var(--light-gray);
        width: 15px;
        height: 2px;
        margin: 0px 10px;
        align-self: center;
      }
    }
  }
  //인증번호 파트
  .auth-btn {
    position: relative;
    display: flex;
    & > button {
      border-radius: 10px;
      border: none;
      width: 50%;
      margin-left: 10px;
      font-weight: 800;
      color: white;
      background-color: var(--light-purple);
    }
    .time {
      font-size: 15px;
      top: 0;
      position: absolute;
      height: 100%;
      width: fit-content;
      display: flex;
      align-items: center;
      @media screen and (min-width: 380px) and (max-width: 400px) {
        left: 36%;
      }
      @media screen and (max-width: 380px) {
        left: 35%;
      }
      @media screen and (min-width: 400px) and (max-width: 460px) {
        left: 33%;
      }
    }
  }
  //확인버튼
  .ready-btn,
  .join-btn {
    border-radius: 10px;
    border: none;
    width: 100%;
    height: 60px;
    font-size: 18px;
    color: gray;
  }
  .join-btn {
    background-color: var(--light-purple);
    color: white;
  }
  ${MediaQuery.small} {
    .form-row {
      width: 100%;
      height: 45px;
      margin-bottom: 15px;
      input,
      select {
        font-size: 15px;
        padding-left: 15px;
        width: 100%;
        height: 100%;
        border-radius: 10px;
        border: 1px solid var(--light-gray);
      }
    }
    //생년월일 파트
    .birth {
      display: flex;
      //주민번호 앞자리와 뒷자리 파트가 40% 60%의 크기를 각자 가져감
      & > *:nth-child(1) {
        width: 35%;
      }
      & > *:nth-child(2) {
        width: 65%;
      }
      //주민번호 뒷자리 파트
      & > div:nth-child(2) {
        display: flex;
        input {
          width: 35px;
          padding-left: 3px;
          text-align: center;
        }
        .masking-part {
          display: flex;
          align-items: center;
          & > div {
            border-radius: 50%;
            height: 13px;
            width: 13px;
            margin-left: 5px;
            background-color: var(--light-gray);
          }
        }
        .line {
          background-color: var(--light-gray);
          width: 15px;
          height: 2px;
          margin: 0px 10px;
          align-self: center;
        }
      }
    }
    //인증번호 파트
    .auth-btn {
      display: flex;
      & > button {
        border-radius: 10px;
        border: none;
        width: 50%;
        margin-left: 10px;
        font-weight: 800;
        color: white;
        background-color: var(--light-purple);
      }
    }
    //확인버튼
    .ready-btn,
    .join-btn {
      border-radius: 10px;
      border: none;
      width: 100%;
      height: 50px;
      font-size: 18px;
      color: gray;
    }
    .join-btn {
      background-color: var(--light-purple);
      color: white;
    }
  }
`;

export const LogoView = styled.div`
  @keyframes up {
    0% {
      opacity: 0;
      transform: translateY(50px);
    }
    100% {
      opacity: 1;
      transform: translateY(0px);
    }
  }
  @keyframes down {
    0% {
      opacity: 0;
      transform: translateY(-50px);
    }
    100% {
      opacity: 1;
      transform: translateY(0px);
    }
  }
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(to bottom, var(--light-purple) 0%, white 100%);
  & > div {
    font-weight: 800;
    padding: 0px 0px 25% 0px;
    animation: down 2.5s forwards;
    font-family: "Arial Rounded MT Bold", "Helvetica Rounded", Arial, sans-serif;
    font-size: 15vw;
    color: #f4eaf9;
    text-shadow: 0 8px 9px var(--light-purple), 0px -2px 1px white;
    letter-spacing: -4px;
  }

  button {
    background-color: white;
    position: relative;
    padding: 12px 35px;
    font-size: 17px;
    font-weight: 800;
    color: var(--light-purple);
    border: none;
    border-radius: 8px;
    box-shadow: 0 0 0 #fec1958c;
    transition: all 0.2.5s ease-in-out;
    cursor: pointer;
    animation: up 2.5s forwards;
    & img {
      width: 15px;
      height: 20px;
    }
  }

  .fil0 {
    fill: #fffdef;
  }
`;
