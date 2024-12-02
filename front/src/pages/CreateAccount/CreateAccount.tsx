import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Form,
  BoldText,
  Header,
  MessageAuthenticationForm,
  Wrapper,
  LogoView,
} from "./CreateAccount.styles";
import { PATH } from "../../constants/path";
import axios, { AxiosError } from "axios";
import { useAuthStore } from "../../store/AuthStore";
import apiClient from "../../axios";

interface JoinUserInfo {
  name: string;
  phone_number: string;
  birth_date: string;
  gender: string;
  telecom: string;
  email: string;
  address: string;
  verification_code: string;
}

const CreateAccount = () => {
  // const baseUrl = import.meta.env.VITE_BASE_URL;
  const baseUrl = `http://localhost:8765/`;
  const navigate = useNavigate();
  const [limitTime, setLimitTime] = useState<number>(2000); // 초기 값 2000으로 설정
  const timerRef = useRef<NodeJS.Timeout | null>(null); //타이머
  const {
    id,
    setUserInfo,
    setMode,
    setAccessToken,
    setRefreshToken,
    setIsLoggedIn,
    setPhoneNumber,
  } = useAuthStore(); // 유저 정보
  const [joinMode, setJoinMode] = useState<boolean>(false); // 회원가입 모드
  const [beforeStarting, setBeforeStarting] = useState<boolean>(true);
  const [validationErrors, setValidationErrors] = useState<{
    [key: string]: boolean;
  }>({});
  const [SMSAuthSent, setSMSAuthSent] = useState<boolean>(false); // 인증번호가 발급되었는지 여부
  const [endSMSAuth, setEndSMSAuth] = useState<boolean>(false); // 인증번호 인증 완료 여부
  const [joinUserInfo, setjoinUserInfo] = useState<JoinUserInfo>({
    name: "이예빈",
    phone_number: "01030170356",
    birth_date: "000511",
    gender: "4",
    telecom: "",
    email: "dpqls0356@gmail.com",
    address: "",
    verification_code: "",
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;

    // 입력 필드 값 업데이트
    setjoinUserInfo({
      ...joinUserInfo,
      [name]: value,
    });

    // 만약 해당 필드가 validationErrors에 존재한다면 errors에서 제거
    if (validationErrors[name]) {
      setValidationErrors((prevErrors) => {
        const { [name]: _, ...rest } = prevErrors; // name 필드를 제외한 나머지 오류만 유지
        return rest;
      });
    }
    //인증번호가 바뀌는 것이 아닌 필드가 바뀌면 인증상태를 false로 변경
    if (name != "verification_code" && name != "email" && name != "address") {
      setSMSAuthSent(false);
      setEndSMSAuth(false);
      if (timerRef.current) {
        clearInterval(timerRef.current);
        setLimitTime(2000);
      }
    }
  };

  /**
   * 날짜 데이터 포맷
   */
  function formatBirthDate(birthDate: string) {
    // 앞 두 자리 연도
    const year =
      parseInt(birthDate.slice(0, 2), 10) < 50
        ? `20${birthDate.slice(0, 2)}` // 2000년대 출생자
        : `19${birthDate.slice(0, 2)}`; // 1900년대 출생자

    // 월과 일 추출
    const month = birthDate.slice(2, 4);
    const day = birthDate.slice(4, 6);

    // yyyy-MM-dd 형식으로 반환
    return `${year}-${month}-${day}`;
  }

  /**
   * 유효성검사
   */
  const validateFields = () => {
    let errors = {};
    // 필수 입력 항목 체크
    if (
      !joinUserInfo.name ||
      joinUserInfo.name.length < 2 ||
      joinUserInfo.name.length > 14
    )
      errors = { ...errors, name: true };
    if (!joinUserInfo.birth_date || joinUserInfo.birth_date.length !== 6)
      errors = { ...errors, birth_date: true };
    if (
      !joinUserInfo.gender ||
      joinUserInfo.gender.length !== 1 ||
      !/^\d+$/.test(joinUserInfo.gender)
    )
      errors = { ...errors, gender: true };
    if (!joinUserInfo.telecom) errors = { ...errors, telecom: true };
    if (
      !joinUserInfo.phone_number ||
      joinUserInfo.phone_number.length !== 11 ||
      !/^\d+$/.test(joinUserInfo.phone_number)
    )
      errors = { ...errors, phone_number: true };

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // 인증번호 제한 시간 타이머
  const startLimitTime = () => {
    let timeRemaining = 180; // 3분 설정
    timerRef.current = setInterval(() => {
      timeRemaining -= 1;
      setLimitTime(timeRemaining);
      if (timeRemaining <= 0) {
        clearInterval(timerRef.current!); // 타이머 중단
      }
    }, 1000); // 1초마다 실행
  };

  // 분:초 형식으로 시간 변환
  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60); // 분 계산
    const seconds = time % 60; // 초 계산
    return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`; // 1:30 형식으로 반환
  };
  /**
   * 1. 인증번호 받아오기
   */
  const getAuthNumber = async () => {
    if (!validateFields()) {
      return; // 유효성 검사 통과하지 못하면 중단
    }
    // 인증번호 발급하기
    try {
      await apiClient.post(
        // `${baseUrl}moapay/member/sendSMS`,
        // `https://j11c201.p.ssafy.io/api/moapay/member/sendSMS`,
        `/api/moapay/member/sendSMS`,
        {
          phoneNumber: joinUserInfo.phone_number,
        },
        {
          withCredentials: true, // 쿠키 또는 인증 정보 포함
        }
      );
      setSMSAuthSent(true); // 인증번호 발급됨
      startLimitTime();
    } catch (e) {
      console.log(e);
    }
    // setSMSAuthSent(true); // 인증번호 발급됨
  };

  /**
   * 2. 인증번호 확인
   */

  const checkAuthNumber = async () => {
    // 유효성 검사 통과하지 못한 경우
    if (!validateFields()) {
      return;
    }
    // 타이머 멈추기
    if (timerRef.current) {
      if (limitTime <= 0) {
        clearInterval(timerRef.current);
        setValidationErrors((prevErrors) => ({
          ...prevErrors,
          timeout: true, // verification_code 필드에 오류 상태 추가
        }));
        setLimitTime(2000);
        return;
      }
    }
    try {
      // 인증번호 확인하기
      const response = await apiClient.post(
        // `${baseUrl}moapay/member/verification`,
        // `https://j11c201.p.ssafy.io/api/moapay/member/verification`,
        `/api/moapay/member/verification`,
        {
          phoneNumber: joinUserInfo.phone_number,
          code: joinUserInfo.verification_code,
        },
        {
          withCredentials: true, // 쿠키 또는 인증 정보 포함
        }
      );
      // 200일때 아래 함수 2개 실행
      if (response?.status == 200) {
        if (timerRef.current) {
          clearInterval(timerRef.current);
        }
        setEndSMSAuth(true);
        setLimitTime(2000);
      }
    } catch (e) {
      const error = e as AxiosError; // AxiosError로 타입 단언
      if (error.response?.status == 400) {
        //인증번호가 틀린 경우 - 인증번호 다시 입력하도록 함
        setValidationErrors((prevErrors) => ({
          ...prevErrors,
          verification_code: true, // verification_code 필드에 오류 상태 추가
        }));
      }
    }
    // setEndSMSAuth(true);
  };

  /**
   * 3. 존재하는 유저인지 판단
   */
  const checkUser = async () => {
    // 유효성 검사 통과하지 못한 경우
    if (!validateFields()) {
      return;
    }

    try {
      // 인증번호가 일치하면 존재하는 멤버인지 확인해야함
      // 요청 결과에 따라 비밀번호 로그인 또는 회원가입으로 전달
      const existUserCheckResponse = await apiClient.post(
        `/api/moapay/member/isMember`,
        // `https://j11c201.p.ssafy.io/api/moapay/member/isMember`,
        // `${baseUrl}moapay/member/isMember`,
        {
          phoneNumber: joinUserInfo.phone_number,
        },
        {
          withCredentials: true, // 쿠키 또는 인증 정보 포함
        }
      );
      console.log(existUserCheckResponse);
      // 회원이 없는 경우
      if (!existUserCheckResponse.data.data.exist) {
        //회원가입
        setMode("Join");
        setJoinMode(true);
      } else {
        setPhoneNumber(existUserCheckResponse.data.data.phoneNumber);
        setUserInfo(
          existUserCheckResponse.data.data.uuid,
          existUserCheckResponse.data.data.name
        );
        setMode("NewLogin");
        navigate(PATH.PASSWORD_LOGIN, {});
      }
    } catch (e) {
      const error = e as AxiosError; // AxiosError로 타입 단언
      console.log(error);
    }

    // test - 회원가입;

    // setJoinMode(true);

    //test - 계정이 있는 경우
    // navigate(PATH.PASSWORD_LOGIN, {
    //   state: {
    //     ment: `앱을 켜려면\n비밀번호를 눌러주세요`,
    //     back: false,
    //     mode: "NewLogin",
    //   },
    // });
  };

  /**
   * 4. 회원가입
   */
  const join = async () => {
    //회원 가입 요청 보내기
    if (!endSMSAuth) return;
    try {
      const response = await apiClient.post(
        // `${baseUrl}moapay/member/join`,
        // `https://j11c201.p.ssafy.io/api/moapay/member/join`,
        `/api/moapay/member/join`,
        {
          name: joinUserInfo.name,
          birthDate: formatBirthDate(joinUserInfo.birth_date),
          gender: Number(joinUserInfo.gender), //1~4로 넘겨주면 F,M 판단해서 db에 넣기
          phoneNumber: joinUserInfo.phone_number,
          email: joinUserInfo.email,
          address: joinUserInfo.address,
        }
      );
      if (response.status == 200) {
        setUserInfo(response.data.data.id, response.data.data.name);
        //로그인 보내기
        const loginResponse = await apiClient.post(
          // `${baseUrl}moapay/member/login`,
          // `https://j11c201.p.ssafy.io/api/moapay/member/login`,
          `/api/moapay/member/login`,
          {
            uuid: response.data.data.id,
            phoneNumber: joinUserInfo.phone_number,
          },
          {
            withCredentials: true, // 쿠키 또는 인증 정보 포함
          }
        );
        if (loginResponse.status == 200) {
          // 응답 받으면 생체인식 설정으로 이동시키기c
          console.log(loginResponse);
          setIsLoggedIn(true);
          setAccessToken(loginResponse.data.data.token.accessToken);
          setRefreshToken(loginResponse.data.data.token.refreshToken);
          navigate(PATH.PASSWORD_LOGIN);
        }
      }
    } catch (e) {
      console.log(e);
    }
    // test
    // localStorage.setItem("hasLoggedInBefore", "true");
    // navigate(PATH.PASSWORD_LOGIN, {
    //   state: {
    //     ment: `간편 비밀번호를 설정합니다.\n 6자리 비밀번호를 입력해주세요`,
    //     back: false,
    //     mode: "Join",
    //   },
    // });
  };

  return (
    <Wrapper>
      {beforeStarting ? (
        <LogoView>
          <div>MoA Pay</div>
          <button
            onClick={() => {
              setBeforeStarting(false);
            }}
          >
            시작하기
          </button>
        </LogoView>
      ) : (
        <MessageAuthenticationForm className={beforeStarting ? "" : "fade-in"}>
          {joinMode ? (
            <Header>
              회원가입을 위해
              <br />
              <BoldText>추가정보</BoldText>를 입력해주세요
            </Header>
          ) : (
            <Header>
              MoA Pay 이용을 위해
              <br />
              <BoldText>본인확인</BoldText>을 진행해주세요
            </Header>
          )}
          <Form>
            {validationErrors.name && (
              <p className="error">이름은 2자 이상 14자 이하여야 합니다.</p>
            )}
            <div className="form-row">
              <input
                maxLength={14}
                type="text"
                placeholder="이름"
                name="name"
                value={joinUserInfo.name}
                onChange={handleChange}
                disabled={endSMSAuth}
                style={{
                  borderColor: validationErrors.name ? "red" : "",
                }}
              />
            </div>
            <div style={{ display: "flex" }}>
              {validationErrors.birth_date && (
                <p style={{ width: "50%" }} className="error">
                  생년월일은 6자리여야 합니다.
                </p>
              )}
              {validationErrors.gender && (
                <p style={{ width: "50%" }} className="error">
                  올바른 숫자를 입력하세요
                </p>
              )}
            </div>
            <div className="form-row birth">
              <input
                type="text"
                placeholder="생년월일"
                name="birth_date"
                value={joinUserInfo.birth_date}
                onChange={handleChange}
                disabled={endSMSAuth}
                style={{
                  borderColor: validationErrors.birth_date ? "red" : "",
                }}
              />
              <div>
                <div className="line"></div>
                <input
                  type="text"
                  name="gender"
                  value={joinUserInfo.gender}
                  onChange={handleChange}
                  disabled={endSMSAuth}
                  style={{
                    borderColor: validationErrors.gender ? "red" : "",
                  }}
                />
                <div className="masking-part">
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
            </div>
            {validationErrors.telecom && (
              <p className="error">통신사를 선택하세요.</p>
            )}
            <div className="form-row">
              <select
                name="telecom"
                value={joinUserInfo.telecom}
                onChange={handleChange}
                disabled={endSMSAuth}
                style={{
                  borderColor: validationErrors.telecom ? "red" : "",
                }}
              >
                <option value="" disabled>
                  통신사를 선택해주세요
                </option>
                <option value="skt">SKT</option>
                <option value="kt">KT</option>
                <option value="lg">LG U+</option>
                <option value="skt_mvno">SKT 알뜰폰</option>
                <option value="kt_mvno">KT 알뜰폰</option>
                <option value="lg_mvno">LG U+ 알뜰폰</option>
              </select>
            </div>
            {validationErrors.phone_number && (
              <p className="error">올바른 전화번호를 입력하세요.</p>
            )}
            <div className="form-row">
              <input
                type="text"
                placeholder="전화번호"
                name="phone_number"
                value={joinUserInfo.phone_number}
                onChange={handleChange}
                disabled={endSMSAuth}
                style={{
                  borderColor: validationErrors.phone_number ? "red" : "",
                }}
              />
            </div>

            {!joinMode && (
              <div>
                {validationErrors.verification_code && (
                  <p style={{ width: "50%" }} className="error">
                    잘못된 인증번호입니다.
                  </p>
                )}
                {validationErrors.timeout && (
                  <p style={{ width: "50%" }} className="error">
                    만료된 인증번호입니다.
                  </p>
                )}
                {endSMSAuth && (
                  <p
                    className="error"
                    style={{ color: endSMSAuth ? "green" : "" }}
                  >
                    인증되었습니다.
                  </p>
                )}
                <div className="form-row auth-btn">
                  <div>
                    <input
                      style={{ borderColor: endSMSAuth ? "green" : "" }}
                      disabled={endSMSAuth}
                      value={joinUserInfo.verification_code}
                      type="text"
                      placeholder="인증번호"
                      name="verification_code"
                      onChange={handleChange}
                    />
                    <div>
                      {limitTime !== 2000 && (
                        <div className="error time">
                          {formatTime(limitTime)}
                        </div>
                      )}
                    </div>
                  </div>
                  {SMSAuthSent ? (
                    <button onClick={checkAuthNumber}>인증하기</button>
                  ) : (
                    <button onClick={getAuthNumber}>인증번호 받기</button>
                  )}
                </div>
              </div>
            )}
            {joinMode && (
              <>
                <div className="form-row">
                  <input
                    type="text"
                    placeholder="이메일"
                    name="email"
                    value={joinUserInfo.email}
                    onChange={handleChange}
                  />
                </div>
                <div className="form-row">
                  <input
                    type="text"
                    placeholder="주소"
                    name="address"
                    value={joinUserInfo.address}
                    onChange={handleChange}
                  />
                </div>
              </>
            )}
            <button
              className={joinMode ? "join-btn" : "ready-btn"}
              onClick={joinMode ? join : checkUser}
              style={{
                backgroundColor: endSMSAuth ? "var(--light-purple)" : "",
                color: endSMSAuth ? "white" : "",
              }}
            >
              {joinMode ? "회원가입" : "확인"}
            </button>
          </Form>
        </MessageAuthenticationForm>
      )}
    </Wrapper>
  );
};

export default CreateAccount;
