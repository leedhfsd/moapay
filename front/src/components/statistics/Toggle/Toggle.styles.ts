import styled from "styled-components";

export const ToggleWrapper = styled.div`
  display: flex;
  justify-content: end;
  position: relative;
  .toggle {
    margin: 20px 10px 20px 0px;
  }
`;

export const Input = styled.input`
  opacity: 0;
`;

export const Label = styled.label<{ checked: boolean }>`
  width: 120px;
  background: ${(props)=>(props.checked?"#FFAAE7":"#D7D9FF")};
  height: 40px;
  display: inline-block;
  border-radius: 50px;
  position: relative;
  transition: all 0.3s ease;
  transform-origin: 20% center;
  border: 0.5px solid rgba(117, 117, 117, 0.31);
  box-shadow: inset 0px 0px 4px 0px rgba(0, 0, 0, 0.2),
    0 -3px 4px rgba(0, 0, 0, 0.15);

  &:before {
    content: "";
    display: block;
    width: ${(props) => (props.checked ? "35px" : "35px")};
    height: ${(props) => (props.checked ? "35px" : "35px")};
    top: 2.5px;
    left: ${(props) => (props.checked ? "80px" : "0.25em")};
    background: #fff;
    border-radius: 2em;
    box-shadow: inset 0.5px -1px 1px rgba(0, 0, 0, 0.35);
    position: absolute;
    transition: all 0.3s ease;
    transform: ${(props) => (props.checked ? "rotate(0)" : "rotate(-25deg)")};
  }
  /* p{
    color:gray;
    position: absolute;
    font-size: 20px;
    top: 10px;
    left: ${(props) => (props.checked ? "1em" : "50px")};
  } */
`;