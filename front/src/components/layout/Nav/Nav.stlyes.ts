import { styled } from "styled-components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import MediaQuery from "../../../constants/styles";
export const Wrapper = styled.div`
  display: flex;
  position: fixed;
  bottom: 0;
  z-index: 100;
  width: 100%;
  background-color: white;
  // background-color: rgba(255, 255, 255, 0.6);
  div {
    flex: 1;
    padding: 20px 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 7px;
    & p {
      font-weight: 500;
      font-size: 15px;
      text-align: center;
    }
  }
  .active {
    & > * {
      color: #c473f6;
    }
  }
  ${MediaQuery.small},${MediaQuery.medium} {
    div {
      padding: 15px 10px;
      & p {
        font-weight: 500;
        font-size: 13px;
        text-align: center;
      }
    }
  }
`;
export const StyledIcon = styled(FontAwesomeIcon)`
  width: 25px;
  height: 25px;
  color: #4a4a4a;
  ${MediaQuery.small} {
    width: 21px;
    height: 21px;
  }
`;
