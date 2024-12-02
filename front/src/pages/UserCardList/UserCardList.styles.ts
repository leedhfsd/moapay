import styled from "styled-components";

export const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  /* justify-content: center; */
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
`;
export const Title = styled.div`
  font-size: 22px;
  font-weight: 900;
  line-height: 30px;
  span {
    font-weight: 800;
  }
`;
export const ImageView = styled.div`
  margin-top: 20%;
  width: 100%;
  img {
    width: 100%;
    height: 100%;
  }
`;
export const Button = styled.div`
  width: 100%;
  text-align: center;
  font-size: 19px;
  background-color: var(--light-purple);
  color: white;
  font-weight: 800;
  margin-top: 30px;
  padding: 20px 0px;
  border-radius: 15px;
`;
export const LoadingView = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  /* From Uiverse.io by mobinkakei */

  .loading-text {
    font-size: 20px;
    font-weight: 800;
    .l {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 0.2s;
    }

    .o {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 0.4s;
    }

    .a {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 0.6s;
    }

    .d {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 0.8s;
    }

    .i {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 1s;
    }

    .n {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 1.2s;
    }

    .g {
      color: black;
      opacity: 0;
      animation: op 2s ease-in-out infinite;
      animation-delay: 1.4s;
    }

    .d1 {
      color: black;
      opacity: 0;
      animation: op1 2s ease-in-out infinite;
      animation-delay: 1.6s;
    }

    .d2 {
      color: black;
      opacity: 0;
      animation: op1 2s ease-in-out infinite;
      animation-delay: 2s;
    }

    .load {
      position: relative;
      width: 1.2em;
      height: 1.2em;
      border-radius: 50%;
    }

    .progress {
      top: 50%;
      left: -80%;
      position: absolute;
      margin-top: 2.2em;
      transform: translate(10%, -50%);
      content: "";
      width: 3.1em;
      height: 3.1em;
      background: transparent;
      border-radius: 50%;
      animation: load_37100 2s ease-in-out infinite;
      animation-delay: 1s;
    }

    @keyframes load_37100 {
      0% {
        background-color: #db4437;
      }

      25% {
        background-color: #f4b400;
      }

      75% {
        background-color: #0f9d58;
      }

      100% {
        background-color: #4285f4;
      }
    }

    .progress:nth-child(2) {
      left: 50%;
      animation-delay: 1.3s;
    }

    .progress:nth-child(3) {
      left: 180%;
      animation-delay: 1.7s;
    }

    .progress:nth-child(4) {
      left: 300%;
      animation-delay: 2s;
    }

    @keyframes op {
      0% {
        color: #aa00ff;
        opacity: 1;
      }

      50% {
        color: black;
        opacity: 1;
      }

      100% {
        color: #aa00ff;
        opacity: 1;
      }
    }

    @keyframes op1 {
      0% {
        color: #aa00ff;
        opacity: 1;
      }

      50% {
        color: black;
        opacity: 0;
      }

      100% {
        color: #aa00ff;
        opacity: 1;
      }
    }
  }
  .loader-3 {
    margin-top: 30px;
    width: 10em;
    display: flex;
    justify-content: space-evenly;
    .circle {
      width: 20px;
      height: 20px;
      border-radius: 50%;
      position: relative;
    }

    .circle:nth-child(1) {
      background-color: #f5edff;
    }

    .circle:nth-child(2) {
      background-color: #c691ff;
    }

    .circle:nth-child(3) {
      background-color: #c691ff;
    }

    .circle:nth-child(4) {
      background-color: #a654ff;
    }

    .circle:nth-child(5) {
      background-color: #aa00ff;
    }

    .circle::before {
      content: "";
      width: 100%;
      height: 100%;
      position: absolute;
      border-radius: 50%;
      opacity: 0.5;
      animation: animateLoader38 2s ease-out infinite;
    }

    .circle:nth-child(1)::before {
      background-color: #f5edff;
    }

    .circle:nth-child(2)::before {
      background-color: #c691ff;
      animation-delay: 0.2s;
    }

    .circle:nth-child(3)::before {
      background-color: #c691ff;
      animation-delay: 0.4s;
    }

    .circle:nth-child(4)::before {
      background-color: #a654ff;
      animation-delay: 0.6s;
    }

    .circle:nth-child(5)::before {
      background-color: #aa00ff;
      animation-delay: 0.8s;
    }

    @keyframes animateLoader38 {
      0% {
        transform: scale(1);
      }

      50%,
      75% {
        transform: scale(2.5);
      }

      80%,
      100% {
        opacity: 0;
      }
    }
  }
`;

export const RequestEnd = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  div {
    font-size: 20px;
    margin-bottom: 30px;
  }
  button {
    font-size: 15px;
    border: none;
    background-color: a var(--light-purple);
  }
`;

// 카드 불러오기
export const CardListWrapper = styled.div`
  height: 100%;
  @keyframes slideUp {
    from {
      transform: translateY(100%); /* 아래에서 시작 */
      opacity: 0;
    }
    to {
      transform: translateY(0); /* 위로 올라옴 */
      opacity: 1;
    }
  }
  /* animation: slideUp 1s ease-in-out; */
  background-color: rgba(255, 255, 255, 0.3);
  padding: 0% 5%;
  height: 100%;
  display: flex;
  flex-direction: column;
  svg {
    fill: black;
  }
`;
export const Top = styled.div`
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: 600;
`;
export const Sub = styled.div`
  margin-bottom: 10px;
`;
export const Main = styled.div`
  overflow-x: hidden;
  .card-row {
    width: 200%;
    position: relative;
    display: flex;
  }
`;
export const EditMode = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
`;
export const CardView = styled.div`
  width: 50%;
  display: flex;
  align-items: center;
  position: relative;
  height: 90px;
  overflow: visible;
  user-select: none;
  div {
    display: flex;
    flex: 1;
    width: 100%;
    display: flex;
    align-items: center;
    height: 90px;
    overflow: visible;
    user-select: none;
    .card-name {
      padding-left: 110px;
    }
  }
`;
export const CardBackground = styled.div`
  width: 50%;
  height: 90px;
  font-size: 15px;
  background-color: rgba(255, 255, 255, 0.5);
  color: black;
  display: flex;
  align-items: center;
  padding-left: 30px;
  margin-left: 20px;
`;
export const AddBtn = styled.div`
  width: 100%;
  text-align: end;
`;
