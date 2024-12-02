import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useAuthStore } from "../../store/AuthStore";
import { List, User, Wrapper } from "./Setting.styles";
import { faUser } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { PATH } from "../../constants/path";

const Setting = () => {
  const { name, setMode } = useAuthStore();
  const navigate = useNavigate();
  return (
    <Wrapper>
      <div className="view">
        <User>
          <FontAwesomeIcon
            style={{
              width: "30px",
              height: "30px",
              color: "white",
              border: "1px solid white",
              padding: "20px",
              borderRadius: "50%",
            }}
            icon={faUser}
          />
          <p>{name}</p>
        </User>
        <List>
          <div
            onClick={() => {
              navigate(PATH.USER_CARD_LIST);
            }}
          >
            내 카드 목록
          </div>
          <div
            onClick={() => {
              navigate(PATH.ADD_CARD);
            }}
          >
            카드 등록하러가기
          </div>
          <div
            onClick={() => {
              setMode("SettingPassword");
              navigate(PATH.PASSWORD_LOGIN);
            }}
          >
            간편 비밀번호 변경
          </div>
          <div
            onClick={() => {
              setMode("SettingPassword");
              navigate(PATH.BIOMETRICS_LOGIN);
            }}
          >
            생체정보 등록/재등록
          </div>
          <div
            onClick={() => {
              localStorage.clear();
              navigate(PATH.HOME);
            }}
          >
            로그아웃
          </div>
        </List>
      </div>
    </Wrapper>
  );
};
export default Setting;
