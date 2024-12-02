import styled from "styled-components";

export const Wrapper = styled.div`
  padding: 10% 10% 120px 10%;
  height: 100%;
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  .view {
    padding: 0 10%;
    background-color: rgba(255, 255, 255, 0.35);
    height: 100%;
    border-radius: 20px;
  }
`;
export const User = styled.div`
  display: flex;
  align-items: center;
  padding: 20px 0px 15px 0px;
  p {
    font-size: 18px;
    margin-left: 20px;
  }
`;
export const List = styled.div`
  /* padding: 20px 10px; */
  div {
    font-size: 18px;
    padding: 20px 0px;
    border-bottom: 1px solid white;
  }
  div:nth-child(1) {
    border-top: 1px solid white;
  }
`;
