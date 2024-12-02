import styled from "styled-components";

export const Wrapper = styled.div`
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  height: 100vh;
  padding: 0px 25px;
  padding-bottom: 10px;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: auto;
  overflow-x: hidden;
  ::-webkit-scrollbar {
    display: none;
  }
  scrollbar-width: none;
  // background: linear-gradient(135deg, #e3a8ff, #c473f6);
`;

export const BackImg = styled.div`
  position: absolute;
  z-index: -1;
`;

export const Top = styled.div`
  display: flex;
  height: 41%;
  flex-direction: column;
  align-items: center;
  /* gap: 40px; */
  padding: 10% 0px;
`;

export const Month = styled.div`
  display: flex;
  justify-content: center;
  gap: 10px;
  width: 100%;
  align-items: center;

  & > svg {
    width: 25px;
  }

  & > div {
    font-size: 32px;
    font-weight: 700;
    // width: 80%;
  }

  & > button {
    background-color: rgba(255, 255, 255, 0);
    border: 0;
    border-radius: 100%;
    width: 32px;
    height: 32px;
  }
`;

export const DateTag = styled.div`
  display: flex;
  gap: 10px;
  width: 60%;
  justify-content: center;
  & > span {
    // width: 30px;
  }
`;

export const CardInfo = styled.div`
  display: flex;

  // gap: 30px;
  width: 100%;
  /* align-items: center; */
  justify-content: space-around;
  gap: 10px;

  & > img {
    width: 30%;
    height: 70%;
    border: 3px solid rgba(255, 255, 255, 0.5);
    border-radius: 10px;
    box-shadow: 0 3px 5px rgba(0, 0, 0, 0.5);
  }

  & > div {
    margin-top: 10px;
    padding-left: 15px;
    display: flex;
    flex-direction: column;
    gap: 7px;
    font-size: 16px;
    width: 100%;
    // font-weight: 700;
    // text-shadow: -1px 0 rgba(255, 255, 255, 0.5), 0 1px rgba(255, 255, 255, 0.5),
    //   1px 0 rgba(255, 255, 255, 0.5), 0 -1px rgba(255, 255, 255, 0.5);
  }

  & > div > div:nth-of-type(1) {
    font-weight: 700;
    font-size: 18px;

    // text-shadow: -1px 0 rgba(255, 255, 255, 0.7), 0 1px rgba(255, 255, 255, 0.7),
    //   1px 0 rgba(255, 255, 255, 0.7), 0 -1px rgba(255, 255, 255, 0.7);
  }
  .benefit-area {
    margin-top: 10px;
    width: 100%;
    display: flex;
    /* justify-content: center; */
    align-items: center;
    p {
      width: 100%;
      padding: 15px 0px;
      border-radius: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 18px;
      /* background-color: rgba(138, 4, 205, 0.2); */
      background-color: rgba(255, 255, 255, 0.5);
    }
  }
`;

export const CardNameInfo = styled.div`
  display: flex;
  gap: 5px;
`;

export const Main = styled.div`
  height: 70%;
  padding: 7% 8%;
  overflow: auto;
  background-color: rgba(255, 255, 255, 0.7);
  border: 2px solid #dbc5e9;
  border-radius: 20px;
  margin: 0px 25px;
`;

export const MainNoBorder = styled.div`
  // min-height: 50%;
  // height: 1000%;
  background-color: rgba(255, 255, 255, 0.5);
  // border: 2px solid #dbc5e9;
  border: 1px solid white;
  margin: 35px 0px 15px 0px;
  border-radius: 10px;
  padding: 10px 7px;
`;

export const Bottom = styled.div`
  position: absolute;
  bottom: 0;
`;

export const DateInput = styled.div`
  display: flex;
  gap: 10px;
  justify-content: space-around;
  padding: 20px 10px;
  align-items: center;
  font-size: 17px;

  & > input {
    width: 100px;
    height: 40px;
    text-align: center;
    border: 2px solid gray;
    border-radius: 10px;
    font-size: 17px;
  }
`;

export const Benefits = styled.div`
  display: flex;
  // background-color: rgba(255, 255, 255, 0.7);
  // padding: 10px 0px;
  // border-radius: 10px;
  // width: 100%;
  justify-content: center;
  gap: 10px;
  width: 100%;
  & > div {
    width: 100%;
  }
  p {
    margin-bottom: 10px;
    font-size: 20px;
  }
  .goal {
    width: 100%;
    text-align: end;
    margin-top: 3px;
  }
`;
export const Progress = styled.div`
  background-color: #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
`;

export const Bar = styled.div`
  background-color: var(--light-purple);
  height: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
`;
export const TopContainer = styled.div`
  display: flex;
  flex-direction: column;
  /* gap: 30px; */
  padding: 25px 0;
  & > div:nth-child(1) {
  }
  & > div:nth-child(2) {
    flex: 1;
  }
`;
