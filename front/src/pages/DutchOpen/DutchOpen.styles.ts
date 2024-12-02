import styled from "styled-components";

export const Wrapper = styled.div`
  position: relative;
  height: 100vh;
  // overflow: auto;
  // width: 100vw;
`

export const Top = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 13% 0;
  text-align: center;
  font-size: 30px;
  font-weight: 700;
`

export const Title = styled.div`
  display:flex;
  align-items:center;
  margin: 0 auto;
`

export const CopyIcon = styled.div`
  position: absolute;
  right: 22px;
`

export const ShareUrl = styled.div`
  // text-align: center;
  margin: 0 auto;
  font-weight: 700;
`

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
`

export const LinkBox = styled.div`
  display: flex;
  align-items: center;
  background-color: #C5CAFF;
  width: 75%;
  border-radius: 7px;
  padding: 4px 5px;
  padding-left: 21.704px;
  margin: 0 auto;
  height: 36px;
  font-size: 18px;
  font-weight: 100;

  & > svg {
    focus:cusor;
  }

  & > input {
    width: 100%;
    // height: 100%;
    font-size: 20px;
    text-align: center;
    background-color: rgba(0,0,0,0);
    border: none;
  }
  :focus{
    outline: none; // 클릭시 테두리 안 생기도록
  }
  // placeholder 설정
  ::-webkit-input-placeholder{
    color: black;
    font-size: 19px;
  }
  
`

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
`