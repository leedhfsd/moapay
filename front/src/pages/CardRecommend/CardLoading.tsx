import React, { useEffect } from "react";
import { gsap } from "gsap";
import {
  Wrapper,
  AniView,
  Scene,
  Box,
  BoxLeftRight,
  BoxFace,
  BoxLid,
  BoxLidInner,
  BoxLidTop,
  BoxLidTopBack,
  BoxLidTopLogo,
  Card1,
  Card2,
  Card3,
  Card4,
  Card5,
  Ment,
} from "./CardLoading.styles";
import { useAuthStore } from "../../store/AuthStore";

// import cardRecommendImg from "./../../../public/assets/gif/cardrecommend.gif"
import cardRecommendImg from "./../../../public/assets/gif/cardrecommendpurple.gif"

const CardLoading: React.FC = () => {
  const { name } = useAuthStore();

  return (
    <Wrapper>
      <Ment>
        {name}님의 소비패턴에 맞는
        <br />
        카드를 추천해드릴게요
      </Ment>
      <AniView>
        <img src={cardRecommendImg} alt="" style={{width: "75%"}}/>
        {/* <div className="CardList">
          <div
            className="card card1 magictime spaceInDown"
            style={{
              animationDelay: "0s",
              animationDuration: "2s",
              animationIterationCount: "infinite",
            }}
          >
            <img src="/assets/image/slide-card.png" />
          </div>
          <div
            className="card card2 magictime spaceInDown"
            style={{
              animationDelay: "3s",
              animationDuration: "2s",
              animationIterationCount: "infinite",
            }}
          >
            <img src="/assets/image/slide-card2.png" />
          </div>
          <div
            className="card card3 magictime spaceInDown"
            style={{
              animationDelay: "7s",
              animationDuration: "2s",
              animationIterationCount: "infinite",
            }}
          >
            <img src="/assets/image/slide-card3.png" />
          </div>
        </div> */}
      </AniView>
    </Wrapper>
  );
};

export default CardLoading;

{
  /* <Scene className="scene">
        <Box className="box">
          <BoxFace className="box__front">
            <div className="box__front-face"></div>
          </BoxFace>
          <BoxFace className="box__back"></BoxFace>
          <BoxLeftRight className="box__left"></BoxLeftRight>
          <BoxLeftRight className="box__right"></BoxLeftRight>
          <BoxLid className="box__lid">
            <BoxLidInner>
              <BoxLidTop>
                <BoxLidTopLogo />
                <BoxLidTopBack />
              </BoxLidTop>
            </BoxLidInner>
          </BoxLid>
          <Card1 className="card--1" />
          <Card2 className="card--2" />
          <Card3 className="card--3" />
          <Card4 className="card--4" />
          <Card5 className="card--5" />
        </Box>
      </Scene> */
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// useEffect(() => {
//   const boxIn = () => {
//     const tl = gsap
//       .timeline()
//       .set(".scene", { autoAlpha: 1 })
//       .fromTo(
//         ".box",
//         { rotationY: -540, y: "100vh" },
//         { duration: 2, rotationY: 0, y: "16vh", ease: "power2" }
//       )
//       .to(
//         ".box__lid",
//         { rotateX: -225, duration: 0.5, ease: "sine.inOut" },
//         "-=1.4"
//       )
//       .to(
//         ".box__lid-flap",
//         { rotateX: 60, duration: 0.5, ease: "sine.inOut" },
//         "-=1.2"
//       )
//       .to(
//         ".box__flap--left",
//         {
//           rotateX: "-=135",
//           duration: 0.5,
//           ease: "sine.inOut",
//           transformOrigin: "50% 100%",
//         },
//         "-=1"
//       )
//       .to(
//         ".box__flap--right",
//         {
//           rotateX: "-=135",
//           duration: 0.5,
//           ease: "sine.inOut",
//           transformOrigin: "50% 100%",
//         },
//         "-=1"
//       );
//     return tl;
//   };

//   const rotationAnim = () => {
//     const tl = gsap
//       .timeline({ repeat: -1 })
//       .to(".box", { rotateY: 180, ease: "power2.inOut", duration: 0.8 })
//       .to(
//         ".card--1",
//         {
//           keyframes: [
//             {
//               duration: 0.4,
//               y: "-155%",
//               transformOrigin: "center bottom",
//               ease: "sine",
//             },
//             {
//               duration: 0.6,
//               rotation: 50,
//               x: "40%",
//               ease: "power2",
//               delay: -0.25,
//             },
//           ],
//         },
//         0.2
//       )
//       .to(
//         ".card--2",
//         {
//           keyframes: [
//             {
//               duration: 0.4,
//               y: "-160%",
//               transformOrigin: "center bottom",
//               ease: "sine",
//             },
//             {
//               duration: 0.6,
//               rotation: 25,
//               x: "20%",
//               ease: "power2",
//               delay: -0.25,
//             },
//           ],
//         },
//         0.2
//       )
//       .to(
//         ".card--3",
//         {
//           keyframes: [
//             {
//               duration: 0.4,
//               y: "-160%",
//               transformOrigin: "center bottom",
//               ease: "sine",
//             },
//             {
//               duration: 0.6,
//               rotation: 0,
//               x: "0%",
//               ease: "power2",
//               delay: -0.25,
//             },
//           ],
//         },
//         0.2
//       )
//       .to(
//         ".card--4",
//         {
//           keyframes: [
//             {
//               duration: 0.4,
//               y: "-160%",
//               transformOrigin: "center bottom",
//               ease: "sine",
//             },
//             {
//               duration: 0.6,
//               rotation: -25,
//               x: "-20%",
//               ease: "power2",
//               delay: -0.25,
//             },
//           ],
//         },
//         0.2
//       )
//       .to(
//         ".card--5",
//         {
//           keyframes: [
//             {
//               duration: 0.4,
//               y: "-155%",
//               transformOrigin: "center bottom",
//               ease: "sine",
//             },
//             {
//               duration: 0.6,
//               rotation: -50,
//               x: "-40%",
//               ease: "power2",
//               delay: -0.25,
//             },
//           ],
//         },
//         0.2
//       )
//       .to(
//         ".card--1",
//         {
//           keyframes: [
//             { duration: 0.6, rotation: 0, x: "0%", ease: "power2.in" },
//             {
//               duration: 0.4,
//               y: "0%",
//               transformOrigin: "center bottom",
//               ease: "sine.in",
//               delay: -0.25,
//             },
//           ],
//         },
//         1.4
//       )
//       .to(
//         ".card--2",
//         {
//           keyframes: [
//             { duration: 0.6, rotation: 0, x: "0%", ease: "power2.in" },
//             {
//               duration: 0.4,
//               y: "0%",
//               transformOrigin: "center bottom",
//               ease: "sine.in",
//               delay: -0.25,
//             },
//           ],
//         },
//         1.4
//       )
//       .to(
//         ".card--3",
//         {
//           keyframes: [
//             { duration: 0.6, rotation: 0, x: "0%", ease: "power2.in" },
//             {
//               duration: 0.4,
//               y: "0%",
//               transformOrigin: "center bottom",
//               ease: "sine.in",
//               delay: -0.25,
//             },
//           ],
//         },
//         1.4
//       )
//       .to(
//         ".card--4",
//         {
//           keyframes: [
//             { duration: 0.6, rotation: 0, x: "0%", ease: "power2.in" },
//             {
//               duration: 0.4,
//               y: "0%",
//               transformOrigin: "center bottom",
//               ease: "sine.in",
//               delay: -0.25,
//             },
//           ],
//         },
//         1.4
//       )
//       .to(
//         ".card--5",
//         {
//           keyframes: [
//             { duration: 0.6, rotation: 0, x: "0%", ease: "power2.in" },
//             {
//               duration: 0.4,
//               y: "0%",
//               transformOrigin: "center bottom",
//               ease: "sine.in",
//               delay: -0.25,
//             },
//           ],
//         },
//         1.4
//       )
//       .to(".box", { rotateY: 360, ease: "power2.inOut", duration: 0.8 }, 1.6);
//     return tl;
//   };

//   const masterTL = gsap.timeline();
//   masterTL.add(boxIn()).add(rotationAnim());
// }, []);
