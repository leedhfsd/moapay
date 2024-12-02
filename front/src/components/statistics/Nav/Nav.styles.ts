import { styled } from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export const NavArea = styled.div`
  width: 100%;
  .nav-header {
    width: 100%;
    display: flex;
  }
  li {
    font-weight: 500;
    color: gray;
    width: 25%;
    display: flex;
    justify-content: center;
    padding: 10px 0px;
    font-size: 18px;
    list-style: none;
    border-radius: 20px;
  }
  .nav-indicator {
    margin-top: 7px;
    position: relative;
    width: calc(100% / 4);
    height: 5px;
    background-color: #ff91e0;
    left: 0px;
    border-radius: 5px;
    transition: all 500ms ease-in-out;
  }
`;
