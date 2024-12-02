import { create } from "zustand";
import { persist } from "zustand/middleware";

interface SavingState {
  savingMode: boolean;
  savingAlram: boolean | null;
  setSavingAlram: (value: boolean) => void;
  setSavingMode: (value: boolean) => void;
}

export const useSavingStore = create<SavingState>()(
  persist(
    (set) => ({
      savingMode: false,
      savingAlram: null,
      setSavingAlram: (value) => set({ savingAlram: value }), // savingAlram 업데이트
      setSavingMode: (value) => set({ savingMode: value }), // savingMode 업데이트
    }),
    {
      name: "saving-storage", // localStorage에 저장될 키 이름
      getStorage: () => localStorage, // 기본적으로 localStorage에 저장
    }
  )
);
