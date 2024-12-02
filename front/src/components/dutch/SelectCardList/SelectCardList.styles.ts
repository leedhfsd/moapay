import styled from "styled-components";

export const Wrapper = styled.div``;

export const CardView = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 3px 20px;

  align-items: center;
  font-size: 15px;
  position: relative; // 카드 내 이미지와 텍스트의 위치 제어를 위해 relative 추가

  & > div {
    width: 100%;
    display: flex;
    align-items: center;
    text-align: left;
    position: relative;
    height: 90px;
    overflow: visible; // 요소가 잘리지 않도록 overflow를 visible로 설정
    user-select: none; // 드래그 방지
  }

  & > div > div {
    width: 100%;
    padding-left: 95px;
    overflow: visible; // 이미지와 텍스트가 카드 경계를 넘을 때 표시되도록 설정
    user-select: none; // 텍스트 드래그 방지
  }

  & > svg {
    margin-left: 15px;
    z-index: 1; // svg가 다른 요소에 가려지지 않게 z-index를 추가
  }

  & > input[type="radio"] {
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
    width: 18px;
    height: 18px;
    border: 2px solid #ccc;
    border-radius: 100%;
    outline: none;
    cursor: pointer;
  }

  & > input[type="radio"]:checked {
    background-color: #547cff;
    border: 2px solid white;
    box-shadow: 0 0 0 2px #c7c7c7;
  }
`;
