import styled from "styled-components";

export const ProductCard = styled.div`
  display: flex;
  border: 1px solid black;
  border-radius: 10px;
  padding: 10px;
  word-break: break-all;
  background-color: rgba(255,255,255, 0.7);
  // display: flex;

  & > div:nth-of-type(1){
    height: 80px;
    width: 80px;
    border-radius: 8px;
    background-color: gray;
  }

  & > div > img {
    height: 100%;
    border-radius: 8px;
  }
`

export const ProductInfo = styled.div`
  display: flex;
  flex-direction: column;
  // justify-content: space-between;
  justify-content: center;
  padding-left: 10px;
  padding-top: 5px;
  font-size: 16px;
  width: 70%;

  & > div:nth-of-type(2) {
    font-size: 12px;
    color: gray;
  }
`