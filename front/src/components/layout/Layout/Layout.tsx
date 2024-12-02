import { Outlet, useLocation, matchPath } from "react-router-dom";
import { Wrapper } from "./Layout.styles";
import Nav from "../Nav/Nav";
import { PATH } from "../../../constants/path";

const Layout = () => {
  const location = useLocation();

  // 동적 세그먼트를 포함한 경로 패턴 확인
  const isNoNav =
    matchPath({ path: PATH.USER_CARD_LIST }, location.pathname) ||
    matchPath({ path: PATH.USER_CARD_DETAIL }, location.pathname);

  return (
    <Wrapper className="LayoutWrapper">
      <Outlet />
      {/* 경로가 일치하지 않을 때만 Nav 표시 */}
      {!isNoNav && <Nav />}
    </Wrapper>
  );
};

export default Layout;
