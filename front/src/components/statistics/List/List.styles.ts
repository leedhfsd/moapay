// import styled from "styled-components";
// import MediaQuery from "../../../constants/styles";

// export const Wrapper = styled.div`
//   padding-top: 13.5px;
//   padding-left: 5px;
//   width: 100%;
//   flex: 1;
//   /* overflow-y: auto; */
//   /* -ms-overflow-style: none;
//   scrollbar-width: none; */
//   .last-box {
//     padding-bottom: var(--padding-bottom);
//     ${MediaQuery.medium},${MediaQuery.small} {
//       padding-bottom: var(--padding-bottom-small);
//     }
//   }
// `;
// export const ListItem = styled.div`
//   display: flex;
//   align-items: center;
//   padding: 15px 0px;
//   border-bottom: 1px solid var(--light-gray);
//   .Col:first-child {
//     & > div {
//       width: 65px;
//       height: 65px;
//       background-color: pink;
//       border-radius: 50%;
//       display: flex;
//       justify-content: center;
//       align-items: center;
//     }
//   }
//   .Col:last-child {
//     padding-left: 15px;
//     width: 100%;
//     height: 70px;
//     display: flex;
//     flex-direction: column;
//     color: gray;
//     p:first-child {
//       margin-top: 5px;
//       font-size: 18px;
//     }
//     p:last-child {
//       font-weight: 600;
//       font-size: 23px;
//       margin-top: 5px;
//     }
//   }
// `;

import styled from "styled-components";
import MediaQuery from "../../../constants/styles";

export const Wrapper = styled.div`
  padding-top: 13.5px;
  padding-left: 5px;
  width: 100%;
  flex: 1;
  /* overflow-y: auto; */
  /* -ms-overflow-style: none;
  scrollbar-width: none; */
  .last-box {
    padding-bottom: var(--padding-bottom);
    ${MediaQuery.medium},${MediaQuery.small} {
      padding-bottom: var(--padding-bottom-small);
    }
  }
`;
export const ListItem = styled.div`
  display: flex;
  align-items: center;
  padding: 10px 0px;
  border-bottom: 1px solid var(--light-gray);
  .Col:first-child {
    & > div {
      width: 55px;
      height: 55px;
      // border: 2px solid #ff91e0;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
  .Col:last-child {
    padding-left: 15px;
    padding-top: 5px;
    width: 100%;
    height: 70px;
    display: flex;
    flex-direction: column;
    color: gray;
    p:first-child {
      margin-top: 5px;
      font-size: 17px;
    }
    p:last-child {
      font-weight: 600;
      font-size: 21px;
      margin-top: 5px;
    }
  }
`;
