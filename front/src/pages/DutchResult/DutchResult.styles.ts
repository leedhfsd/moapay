import styled from "styled-components";

export const Container = styled.div`
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  max-width: 400px;
  margin: auto;
`;

export const Reload = styled.div`
  text-align: right;
  margin-right: 30px;
`;

export const Header = styled.h2`
  font-size: 36px;
  font-weight: bold;
  margin: 10px 10px 10px 10px;
  text-align: center;
`;

export const Button = styled.button`
  background-color: #6c63ff;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #5145e6;
  }
`;

export const MemberList = styled.ul`
  list-style-type: none;
  /* padding-top: 10%; */
`;

export const Merchant = styled.h3`
  font-size: 24px;
  margin: 10px;
  text-align: center;
`;

export const MemberItem = styled.li`
  padding: 20px;
  background-color: #fff;
  border-radius: 6px;
  margin-bottom: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

export const MemberStatus = styled.span`
  color: green;
  font-weight: bold;
  margin-right: 10px;
`;

export const Wrapper = styled.div`
  position: relative;
  height: 100vh;
  /* overflow: auto; // [추후 삭제 예정] 방 생성 확인용으로 추가함 */
  // width: 100vw;
`;

export const Main = styled.div`
  position: relative;
  width: 85%;
  height: 100vh;
  border: 3px solid black;
  border-top-right-radius: 30px;
  border-top-left-radius: 30px;
  background-color: rgba(255, 255, 255, 0.65);
  margin: 0 auto;
  padding: 20px 25px;
  z-index: 1;
`;

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
`;
