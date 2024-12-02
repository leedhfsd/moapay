import { create } from "zustand";
import { persist } from "zustand/middleware";

interface AuthState {
  id: string | null; // 최소한의 사용자 식별 정보
  name: string | null; // 사용자 이름
  isLoggedIn: boolean; // 로그인 여부
  bioLogin: boolean;
  phoneNumber: string | null;
  mode: string;
  accessToken: string | null;
  refreshToken: string | null;
  paymentType: string | null;
  savingMode: boolean;
  setSavingMode: (value: boolean) => void;
  setPaymentType: (value: string) => void;
  setAccessToken: (value: string) => void;
  setRefreshToken: (value: string) => void;
  setBioLogin: (value: boolean) => void;
  setIsLoggedIn: (value: boolean) => void;
  setUserInfo: (id: string, name: string) => void;
  setMode: (value: string) => void;
  setPhoneNumber: (value: string) => void;
  Login: () => void; // 로그인 상태를 설정하는 함수
  Logout: () => void; // 로그아웃 함수
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      id: null,
      name: null,
      isLoggedIn: false,
      bioLogin: false,
      mode: "",
      accessToken: null,
      refreshToken: null,
      phoneNumber: null,
      paymentType: null,
      savingMode: false,
      setSavingMode: (value) => set({ savingMode: value }),
      setPaymentType: (value) => set({ paymentType: value }),
      setPhoneNumber: (value) => set({ phoneNumber: value }),
      setAccessToken: (value) => set({ accessToken: value }),
      setRefreshToken: (value) => set({ refreshToken: value }),
      setBioLogin: (value: boolean) => set({ bioLogin: value }),
      setMode: (value) => set({ mode: value }),
      setIsLoggedIn: (value) => set({ isLoggedIn: value }),
      setUserInfo: (id, name) => set({ id, name }),

      Login: () => {
        set({ isLoggedIn: true }); // 여기서 직접 상태 업데이트
      },
      Logout: () => {
        set({ isLoggedIn: false, id: null, name: null }); // 로그아웃 시 상태 초기화
      },
    }),
    {
      name: "auth-storage", // localStorage에 저장될 키 이름
      getStorage: () => localStorage, // 기본적으로 localStorage에 저장
    }
  )
);
