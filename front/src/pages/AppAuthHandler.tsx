import React, { useEffect } from "react";
import { useNavigate, Outlet } from "react-router-dom";
import { PATH } from "../constants/path";
import { useAuthStore } from "../store/AuthStore";

const AppAuthHandler: React.FC = () => {
  const { isLoggedIn, setIsLoggedIn, bioLogin } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.setItem("lastInURI", window.location.pathname);
    //앱의 시각 상태 변경되었을 때 실행될 함수
    const handleVisibilityChange = () => {
      //백그라운드로 옮겨질 경우
      if (document.hidden) {
        //lastBackgroundTime 업데이트하기
        localStorage.setItem("lastBackgroundTime", Date.now().toString());
      }
      //앱으로 돌아온 경우
      else {
        //마지막 활성시간 가져오기 = 백그라운드로 가기 전의 시간 가져오기
        const lastBackgroundTime = localStorage.getItem("lastBackgroundTime");
        //백그라운드로 가기 전의 시간이 있는 경우 - 앱 백그라운드 에서 되돌아온 경우로 이게 없으면 앱을 아예 꺼버린 것
        if (lastBackgroundTime) {
          const timeDiff = Date.now() - parseInt(lastBackgroundTime, 10);

          //백그라운드에서 머무른 시간이 1분을 초과한 경우
          if (timeDiff > 60000) {
            // console.log("step1");
            //이전 로그인 기록이 있는 경우
            if (isLoggedIn) {
              // console.log("step2-1");
              requestLogin();
            }
            //이전 로그인 기록이 없는 경우
            else {
              // console.log("step2-2");
              requestCreateAccount();
            }
          }
        }
      }
    };

    //앱이 시작될 때 실행될 함수
    const handleAppStart = () => {
      // 마지막 활성화 시간을 localStorage에서 가져옵니다.
      const lastActiveTime = localStorage.getItem("lastActiveTime");

      // 사용자가 이전에 로그인한 적이 있는지 확인합니다.
      // "hasLoggedInBefore" 값이 "true"이면 이전에 로그인한 상태를 의미합니다.

      // 만약 lastActiveTime이 없으면 앱이 처음 시작되었거나 완전히 종료되었다가 다시 실행된 것입니다.
      if (!lastActiveTime) {
        // console.log(1);
        // 사용자가 이전에 로그인한 적이 있는 경우, 간편 로그인 화면으로 이동합니다.
        if (isLoggedIn) {
          // console.log("1-1");
          requestLogin();
        }
        // 사용자가 이전에 로그인한 적이 없으면 회원가입 페이지로 이동합니다.
        else {
          // console.log("1-2");
          requestCreateAccount();
        }
      }
      // lastActiveTime이 있으면, 사용자가 앱을 백그라운드에 두었다가 다시 돌아온 상태입니다.
      else {
        // console.log("2");
        const lastInURI =
          (localStorage.getItem("lastInURI") !== "/"
            ? localStorage.getItem("lastInURI")
            : "/home") || "/home";
        // lastInURI가 null이 아닌 경우에만 navigate 호출
        if (lastInURI) {
          if (isLoggedIn && lastInURI == "/create-account") {
            localStorage.setItem("lastInURI", PATH.HOME);
            navigate(PATH.HOME);
          }
          navigate(lastInURI);
        }
      }

      // 마지막 활성화 시간을 현재 시간으로 localStorage에 저장하여 추후 비교에 사용할 수 있도록 합니다.
      localStorage.setItem("lastActiveTime", Date.now().toString());
    };

    //로그인
    const requestLogin = () => {
      //생체정보가 있는 경우 생체 인식으로
      if (bioLogin == true) {
        navigate(PATH.BIOMETRICS_LOGIN);
      }
      //생체정보가 없는 경우 비밀번호 입력으로
      else {
        navigate(PATH.PASSWORD_LOGIN);
      }
    };

    //회원가입
    const requestCreateAccount = () => {
      navigate(PATH.CREATE_ACCOUNT);
    };

    handleAppStart();
    document.addEventListener("visibilitychange", handleVisibilityChange);

    return () => {
      document.removeEventListener("visibilitychange", handleVisibilityChange);
    };
  }, [navigate, setIsLoggedIn, window.location.pathname]);

  return <Outlet />; // 자식 경로를 렌더링
};

export default AppAuthHandler;
