import styled from "styled-components";
export const Layout = styled.div`
  position: relative;
  width: 100%;
  .first-wave {
    position: absolute;
    background-color: #a875f4;
    border: none;
    z-index: 2;
    border-radius: 50%;
    height: 500px;
    width: 1000px;
    top: -264px;
    left: -500px;
  }
`;
export const WaveDiv = styled.div`
  width: 100%;
  & > div {
    position: absolute;
    width: 20000px;
    height: 230px;
    transform: translate3d(0, 0, 0);
  }
  .second-wave {
    top: 120px;
    z-index: 1;
    background: url("/assets/image/secondWave.png") repeat-x;
    animation: first-wave 8s cubic-bezier(0.36, 0.45, 0.63, 0.53) infinite;
  }
  .last-wave {
    top: 120px;
    background: url("/assets/image/lastWave.png") repeat-x;
    z-index: 0;
    animation: second-wave 24s cubic-bezier(0.36, 0.45, 0.63, 0.53) infinite;
  }

  @keyframes first-wave {
    0% {
      margin-left: -10px;
    }
    100% {
      margin-left: -1600px;
    }
  }
  @keyframes second-wave {
    0% {
      margin-left: -10px;
    }
    100% {
      margin-left: -2733px;
    }
  }
`;
