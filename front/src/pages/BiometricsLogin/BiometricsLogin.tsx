import axios from "axios"; // axios 임포트
import { Button, Header, Wrapper } from "./BiometricsLogin.styles";
import { useNavigate } from "react-router-dom";
import { PATH } from "../../constants/path";
import { useState } from "react";
import { useAuthStore } from "../../store/AuthStore";

const BiometricsLogin = () => {
  // const baseUrl = import.meta.env.VITE_BASE_URL;
  const baseUrl = `http://localhost:18040/`;
  const navigate = useNavigate();
  const { name, accessToken, mode } = useAuthStore();
  const [error, setError] = useState<boolean>(false);

  // Base64 URL로 인코딩된 문자열을 Uint8Array로 변환하는 함수
  const base64UrlToUint8Array = (base64Url: string): Uint8Array => {
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const paddedBase64 = base64.padEnd(
      base64.length + ((4 - (base64.length % 4)) % 4),
      "="
    );
    const binaryString = atob(paddedBase64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
  };

  const handleBiometricLogin = async () => {
    try {
      // localStorage에서 WebAuthn id 가져오기
      const storedId = localStorage.getItem("webauthnId");
      if (!storedId) {
        throw new Error("등록된 생체 인증 ID가 없습니다.");
      }

      // Base64 URL로 인코딩된 storedId를 Uint8Array로 변환
      const idUint8Array = base64UrlToUint8Array(storedId);

      // 변환된 id를 publicKeyCredentialRequestOptions에 사용
      const publicKeyCredentialRequestOptions: PublicKeyCredentialRequestOptions =
        {
          challenge: new Uint8Array([
            0x8c, 0x84, 0x47, 0x7c, 0x52, 0xbc, 0x9a, 0x23, 0x45, 0x32, 0x98,
            0xaf, 0xa7, 0xbc, 0x6e, 0x99,
          ]),
          allowCredentials: [
            {
              id: idUint8Array, // Base64 URL에서 Uint8Array로 변환된 id 사용
              type: "public-key",
              transports: ["internal" as AuthenticatorTransport], // 올바른 타입 설정
            },
          ],
          userVerification: "preferred",
          timeout: 60000, // 60초로 설정
        };

      // WebAuthn API 호출
      const credential = await navigator.credentials.get({
        publicKey: publicKeyCredentialRequestOptions,
      });
      console.log("credential");
      console.log(credential);

      if (credential) {
        if (mode == "SettingPasswords") {
          navigate(PATH.SETTING_BIOMETRICS_LOGIN);
        } else {
          navigate(PATH.HOME);
        }
      } else {
        setError(true);
      }
    } catch (err) {
      console.error("인증 중 오류 발생:", err);
    }
  };

  return (
    <Wrapper>
      <div className="area">
        <Header>
          {mode === "SettingPassword" ? (
            <>
              <h1>생체 인증</h1>
            </>
          ) : (
            <>
              {" "}
              <h1>생체 로그인</h1>
            </>
          )}

          <p style={{ whiteSpace: "pre-wrap", textAlign: "center" }}>
            {error
              ? "잘못된 인증입니다.\n다시 시도하세요"
              : "생체 인증을 진행해주세요"}
          </p>
          <div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              className="size-6"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M7.864 4.243A7.5 7.5 0 0 1 19.5 10.5c0 2.92-.556 5.709-1.568 8.268M5.742 6.364A7.465 7.465 0 0 0 4.5 10.5a7.464 7.464 0 0 1-1.15 3.993m1.989 3.559A11.209 11.209 0 0 0 8.25 10.5a3.75 3.75 0 1 1 7.5 0c0 .527-.021 1.049-.064 1.565M12 10.5a14.94 14.94 0 0 1-3.6 9.75m6.633-4.596a18.666 18.666 0 0 1-2.485 5.33"
              />
            </svg>
            <img
              width="96"
              height="96"
              src="https://img.icons8.com/external-tanah-basah-basic-outline-tanah-basah/96/external-face-scan-smart-home-tanah-basah-basic-outline-tanah-basah.png"
              alt="external-face-scan-smart-home-tanah-basah-basic-outline-tanah-basah"
            />
          </div>
        </Header>
        <Button onClick={handleBiometricLogin}>인증하기</Button>
        <div
          style={{ marginTop: "20px" }}
          onClick={() => {
            navigate(PATH.PASSWORD_LOGIN, {
              state: {
                ment: `로그인을 위해.\n비밀번호를 입력해주세요`,
                back: false,
                mode: "Login",
              },
            });
          }}
        >
          간편 비밀번호로 로그인
        </div>
      </div>
    </Wrapper>
  );
};

export default BiometricsLogin;
