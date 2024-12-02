import styled from "styled-components";

export const Wrapper = styled.div`
  height: 100%;
`;

export const Log = styled.div`
  font-size: 21px;
  display: flex;
  flex-direction: column;
  // gap: 10px;
`;

export const Date = styled.div`
  font-size: 16px;
  font-size: 16px;
  margin-left: 8px;
  font-weight: 700;
  // text-shadow: -1px 0 rgba(255, 255, 255, 0.8), 0 1px rgba(255, 255, 255, 0.8),
  //   1px 0 rgba(255, 255, 255, 0.8), 0 -1px rgba(255, 255, 255, 0.8);
`;

export const Content = styled.div`
  position: relative;
  margin: 10px 10px;
  & > div {
    display: flex;
    // justify-content: space-around;
    // gap: 10px;
    padding: 10px 0px;
  }

  & > div {
    display: flex;
    justify-content: space-between;
    align-items: center;
    // gap: 10px;
  }

  // & > div > div:nth-of-type(1) {
  //   width: 45px;
  //   height: 45px;
  //   border-radius: 100%;
  //   text-align: center;
  //   position: relative;
  //   display: flex;
  //   align-items: center; /* 수직 중앙 정렬 */
  //   justify-content: center; /* 수평 중앙 정렬 */
  // }

  // & > div > div:nth-of-type(1) > img {
  //   // width: 60%;
  //   // height: auto; /* 이미지의 비율을 유지하면서 너비에 맞춰 크기 조정 */
  // }

  & > hr {
    border: 1px solid #b97df9;
  }
`;

export const DetailLeft = styled.div`
  display: flex;
  // justify-content: space-around;
  // gap: 10px;
  // padding: 20px 0px;

  & > div:nth-of-type(1) {
    width: 45px;
    height: 45px;
    border-radius: 100%;
    text-align: center;
    position: relative;
    display: flex;
    align-items: center; /* 수직 중앙 정렬 */
    justify-content: center; /* 수평 중앙 정렬 */
  }

  & > div:nth-of-type(1) > img {
    // width: 60%;
    // height: auto; /* 이미지의 비율을 유지하면서 너비에 맞춰 크기 조정 */
  }
`;

export const Detail = styled.div`
  // width: 70%;
  display: flex;
  flex-direction: column;
  margin-left: 16px;
  // gap: 4px;

  & > div:nth-of-type(2) {
    font-size: 14px;
    color: #626262;
  }

  // & > div:nth-of-type(3) {
  //   position: absolute;
  //   right: 0;
  //   bottom: 25px;
  // }
`;

export const Price = styled.div`
  font-size: 16px;
  align-items: center;
  width: 25%;
  white-space: nowrap;
  display: flex;
  justify-content: end;
`;

export const EmptyLogs = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  // margin: 0 auto;
  padding:  20% 0;

  & > div {
    font-size: 32px;
    font-size: 29px;
    font-weight: 700;
    margin-top: 24px;
  }

  & > img {
    width: 60%;
  }
`;
