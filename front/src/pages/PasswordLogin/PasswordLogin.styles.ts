import styled from "styled-components";
import MediaQuery from "../../constants/styles";
export const Wrapper = styled.div`
  /* @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  } */
  /* background-color: var(--light-purple); */
  background: linear-gradient(120deg, #f1e5ff 6%, #dcbefc 50%, #ffc6ff);
  padding: 20% 10% 20% 10%;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  /* animation: fadeIn 0.5s forwards; */
`;
export const Nav = styled.div``;
export const Container = styled.div`
  height: 100%;
  width: 100%;
  border-radius: 50px;
  /* border-bottom-right-radius: 0;
  border-bottom-left-radius: 0; */
  padding: 30% 30px;
  /* background-color: white; */
  background-color: rgba(255, 255, 255);
  display: flex;
  flex-direction: column;
  align-items: center;
  ${MediaQuery.small} {
    padding: 20% 30px;
  }
`;
export const Ment = styled.div`
  text-align: center;
  color: black;
  line-height: 30px;
  font-size: 19px;
  font-weight: 560;
  @media screen and (max-width: 400px) {
    font-size: 17px;
  }
`;
export const PasswordView = styled.div`
  @keyframes ddiyoung {
    0% {
    }
    50% {
    }
    100% {
    }
  }
  padding: 10% 0px;
  display: flex;
  justify-content: center;
  width: 100%;
  div {
    color: var(--light-gray);
    margin-right: 10px;
  }
  .full {
    color: var(--light-purple);
    animation: ddiyoung 1s forwards;
  }
`;
export const KeyPad = styled.div`
  width: 100%;
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(4, 1fr);
  place-items: center;
  button {
    width: 60px;
    height: 60px;
    border: none;
    border-radius: 10px;
    font-size: 20px;
    font-weight: 800;
    @media screen and (min-width: 400px) {
      width: 70px;
      height: 70px;
    }
  }
  button.active {
    background-color: var(--light-purple);
    color: white;
  }
  button:active {
    background-color: var(--light-purple);
    color: white;
  }
  ${MediaQuery.small} {
    button {
      width: 50px;
      height: 50px;
      border: none;
      border-radius: 10px;
      font-size: 20px;
      font-weight: 800;
    }
  }
`;
