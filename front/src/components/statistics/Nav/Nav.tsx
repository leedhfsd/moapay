import { NavArea } from "./Nav.styles";
type NavProps = {
  navPosition: string;
  changeComponent: (index: number) => void;
};
const Nav: React.FC<NavProps> = ({ navPosition, changeComponent }) => {
  return (
    <NavArea>
      <div className="nav-header">
        <li
          onClick={() => {
            console.log("nav", 0);
            changeComponent(0);
          }}
        >
          소비
        </li>
        <li
          onClick={() => {
            console.log("nav", 1);
            changeComponent(1);
          }}
        >
          혜택
        </li>
        <li
          onClick={() => {
            console.log("nav", 2);
            changeComponent(2);
          }}
        >
          통계
        </li>
        <li
          onClick={() => {
            console.log("nav", 3);
            changeComponent(3);
          }}
        >
          절약
        </li>
      </div>
      <div style={{ left: `${navPosition}` }} className="nav-indicator"></div>
    </NavArea>
  );
};

export default Nav;
