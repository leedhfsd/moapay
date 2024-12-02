import styled from "styled-components";

export const Wrapper = styled.div`
  position: relative;
  z-index: 1000;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  .magictime {
    animation-iteration-count: infinite;
  }
`;
export const Ment = styled.div`
  text-align: center;
  font-size: 20px;
  line-height: 30px;
`;
export const AniView = styled.div`
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  .CardList {
    position: relative;
    width: 180px; /* 카드의 가로 크기 */
    height: 180px; /* 카드의 세로 크기 */
  }

  .card {
    position: absolute;
    width: 100%;
    height: 100%;
    opacity: 0; /* 초기 상태는 보이지 않게 설정 */
    transform-origin: center center; /* 중앙에서 애니메이션 회전 */
    img {
      width: 100%;
      height: 100%;
    }
  }
`;

// Styled Components
export const Scene = styled.div`
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  perspective: 900px;
  visibility: hidden;
`;

export const Box = styled.div`
  transform-style: preserve-3d;
  position: relative;
  z-index: 2;
  width: 180px;
  height: calc(180px * 1.387);
  transform-style: preserve-3d;
`;

export const BoxFace = styled.div`
  position: absolute;
  width: 100%;
  height: 100%;
  background: #ffffff;
`;

export const BoxLeftRight = styled(BoxFace)`
  top: 0;
  left: calc(50% - calc(180px * 0.27355) / 2);
  width: calc(180px * 0.27355);
  background: #ffffff;
`;

export const BoxFlap = styled.div`
  position: absolute;
  left: calc(50% - calc(180px * 0.27355) / 2);
  top: calc(50% - calc(180px * 0.27355) / 2);
  width: calc(180px * 0.27355);
  height: 0;
  transform-origin: 50% 100%;
  border-bottom: calc(180px * 0.27355) solid #dedede;
  border-left: calc(calc(180px * 0.27355) / 10) solid transparent;
  border-right: calc(calc(180px * 0.27355) / 10) solid transparent;
  background: #ffffff;
`;

export const BoxLid = styled.div`
  position: absolute;
  z-index: 0;
  top: calc(50% - calc(180px * 0.27355) / 2);
  width: 180px;
  height: calc(180px * 0.27355);
  transform-origin: 50% 100%;
  transform-style: preserve-3d;
  transform: translateZ(calc(-calc(180px * 0.27355) / 2))
    translateY(
      calc(-calc(180px * 1.387) / 2 - calc(180px * 0.27355) / 2 + 0.5px)
    )
    rotateY(180deg) rotateX(90deg);
  background: #ffffff;
`;

export const BoxLidInner = styled.div`
  position: relative;
  height: 100%;
  z-index: 0;
  transform-style: preserve-3d;
  background: #ffffff;
`;

export const BoxLidTop = styled.div`
  width: 180px;
  height: calc(180px * 0.27355);
  transform-style: preserve-3d;
  background: #ffffff;
`;

export const BoxLidTopLogo = styled.div`
  position: absolute;
  z-index: 0;
  top: 0;
  left: 0;
  width: 180px;
  height: calc(180px * 0.27355);
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/coolclub-logo-full.png)
    white no-repeat center center;
  background-blend-mode: multiply;
  background-size: cover;
  transform: rotateX(0deg);
  backface-visibility: hidden;
`;

export const BoxLidTopBack = styled.div`
  position: absolute;
  left: 0;
  top: 0;
  width: 180px;
  height: calc(180px * 0.27355);
  background: #ececec;
  background-image: linear-gradient(
    -145deg,
    rgba(0, 0, 0, 0.2) 0%,
    rgba(236, 236, 236, 0.5) 72%,
    rgba(white, 0.3) 73%
  );
  transform: rotateX(180deg);
  backface-visibility: hidden;
  background: #ececec;
`;

export const Card = styled.div`
  position: absolute;
  left: calc((180px - calc(180px * 0.6)) / 2);
  top: -5%;
  width: calc(180px * 0.6);
  height: calc(180px * 0.6 * 1.3944);
  background: black;
  backface-visibility: hidden;
  transform-style: preserve-3d;

  &:after {
    position: absolute;
    content: "";
    width: 100%;
    height: 100%;
    background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-back.png)
      no-repeat left top;
    background-size: cover;
    backface-visibility: hidden;
    transform: rotateY(180deg);
    transform-style: preserve-3d;
  }
`;

export const Card1 = styled(Card)`
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-01.png)
    no-repeat left top;
  background-size: cover;
  transform: translateZ(0px) rotateY(180deg);
`;

export const Card2 = styled(Card)`
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-02.png)
    no-repeat left top;
  background-size: cover;
  transform: translateZ(calc(180px / 50)) rotateY(180deg);
`;

export const Card3 = styled(Card)`
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-03.png)
    no-repeat left top;
  background-size: cover;
  transform: translateZ(calc(180px / 50 * 2)) rotateY(180deg);
`;

export const Card4 = styled(Card)`
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-04.png)
    no-repeat left top;
  background-size: cover;
  transform: translateZ(calc(180px / 50 * 3)) rotateY(180deg);
`;

export const Card5 = styled(Card)`
  background: url(https://s3-us-west-2.amazonaws.com/s.cdpn.io/61488/fwa-cards-05.png)
    no-repeat left top;
  background-size: cover;
  transform: translateZ(calc(180px / 50 * 4)) rotateY(180deg);
`;
