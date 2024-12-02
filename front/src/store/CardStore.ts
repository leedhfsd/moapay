import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface payLog {
  amount: number;
  benefitBalance: number;
  categoryId: string;
  createTime: string;
  merchantName: string;
}

export interface categoryData {
  categoryId: string;
  money: number;
  per: number;
}

export const categoryImages: { [key: string]: string } = {
  C0000: "ALL.svg",
  C0001: "간편결제.svg",
  C0002: "교육.svg",
  C0003: "교통.svg",
  C0004: "마트·편의점.svg",
  C0005: "미용.svg",
  C0006: "보험.svg",
  C0007: "생활.svg",
  C0008: "쇼핑.svg",
  C0009: "숙박.svg",
  C0010: "식비.svg",
  C0011: "연회비.svg",
  C0012: "온라인·쇼핑.svg",
  C0013: "의료.svg",
  C0014: "자동차.svg",
  C0015: "주거·통신.svg",
  C0016: "주유.svg",
  C0017: "취미.svg",
  C0018: "카페.svg",
  C0019: "항공·여행.svg",
  C0020: "해외.svg",
};

// 혜택 인터페이스 정의
export interface Benefit {
  categoryName: string;
  benefitDesc: string;
  categoryType: string; // 카테고리 타입은 실제 문자열
  benefitType: string; // 혜택 타입도 문자열
  benefitValue: number; // 혜택 값은 실제로 숫자형
  benefitPoint: number; // 혜택 포인트 추가
}

// 카드 객체 타입 정의
export interface CardProduct {
  cardProductUuid: string;
  cardProductName: string; // 카드명
  cardProductCompanyName: string; // 카드 발급사
  cardProductBenefitTotalLimit: number | null; // 혜택 한도 (nullable)
  cardProductType: string; // 카드 종류 (신용, 체크 등)
  cardProductAnnualFee: number; // 국내 연회비
  cardProductAnnualFeeForeign: number; // 해외 연회비
  cardProductPerformance: number; // 전월 실적
  cardProductImgUrl: string; // 이미지 url
  cardBenefits: Benefit[] | null; // 혜택 목록
}

export interface Accounts {
  accountUuid: string;
  accountNumber: string;
  balance: number;
}
// 카드 객체 타입 정의
export interface Card {
  uuid: string; //uuid 카드 식별자
  cardNumber: string; // 카드번호
  cvc: string; // CVC 코드 추가
  performanceFlag: boolean; // 실적 달성 여부
  cardLimit: number; // 카드 한도
  amount: number; // 사용 금액
  benefitUsage: number; // 사용한 혜택량
  cardProduct: CardProduct; // 카드 정보 객체
  accounts: Accounts;
}

// 카드 상태 인터페이스 정의
interface CardState {
  cardWithNullName: Card;
  cardWithDividPay: Card;
  cardList: Card[]; // 카드 리스트 배열
  recommendCardList: CardProduct[];
  setRecommendCardList: (newRecommendCardList: CardProduct[]) => void;
  setCardList: (newCardList: Card[]) => void; // 받아온 카드 리스트를 설정하는 함수
  addCard: (card: Card) => void; // 카드 추가 함수
  removeCard: (index: number) => void; // 카드 삭제 함수
  clearCards: () => void; // 카드 리스트 초기화 함수
  getCardByNumber: (cardNumber: string) => Card | undefined;
  getCardByUuid: (uuid: string) => Card | undefined;
  getCategoryImage: (categoryId: string) => string;
}

export const useCardStore = create<CardState>()(
  persist(
    (set, get) => ({
      cardWithNullName: {
        uuid: "add-card",
        cardNumber: "", // 빈 문자열
        cvc: "", // 빈 문자열
        performanceFlag: false, // 실적 달성 여부
        cardLimit: 0, // 카드 한도 0
        amount: 0, // 사용 금액 0
        benefitUsage: 0, // 사용한 혜택량 0
        cardProduct: {
          cardProductUuid: "", // 빈 문자열
          cardProductName: "", // 카드명 null 대신 빈 문자열
          cardProductCompanyName: "", // 발급사 빈 문자열
          cardProductBenefitTotalLimit: 0, // 혜택 한도 0
          cardProductType: "", // 카드 종류 빈 문자열
          cardProductAnnualFee: 0, // 국내 연회비 0
          cardProductAnnualFeeForeign: 0, // 해외 연회비 0
          cardProductPerformance: 0, // 전월 실적 0
          cardProductImgUrl: "", // 이미지 URL 빈 문자열
          cardBenefits: [], // 혜택 목록 빈 배열
        },
        accounts: {
          accountUuid: "", // 빈 문자열
          accountNumber: "", // 빈 문자열
          balance: 0, // 잔액 0
        },
      },
      cardWithDividPay: {
        uuid: "recommended-card",
        cardNumber: "", // 예시 카드 번호
        cvc: "", // 예시 CVC 코드
        performanceFlag: false, // 실적 달성 여부
        cardLimit: 0, // 카드 한도 1,000,000
        amount: 0, // 사용 금액 500,000
        benefitUsage: 0, // 사용한 혜택량 10,000
        cardProduct: {
          cardProductUuid: "", // 예시 UUID
          cardProductName: "", // 카드명 dividPay
          cardProductCompanyName: "", // 발급사 예시
          cardProductBenefitTotalLimit: 0, // 혜택 한도 100,000
          cardProductType: "", // 카드 종류 신용
          cardProductAnnualFee: 0, // 국내 연회비 5,000
          cardProductAnnualFeeForeign: 0, // 해외 연회비 10,000
          cardProductPerformance: 0, // 전월 실적 30
          cardProductImgUrl: "card.png", // 이미지 URL card.png
          cardBenefits: [], // 혜택 목록 빈 배열
        },
        accounts: {
          accountUuid: "", // 예시 계좌 UUID
          accountNumber: "", // 예시 계좌번호
          balance: 500000, // 잔액 500,000
        },
      },
      recommendCardList: [],
      setRecommendCardList: (newRecommendCardList: CardProduct[]) =>
        set({
          recommendCardList: newRecommendCardList.map((card) => ({
            ...card,
            cardProductImgUrl: card.cardProductImgUrl.replace(/\s/g, "_"),
          })),
        }),
      cardList: [],
      setCardList: (newCardList: Card[]) =>
        set({
          cardList: newCardList.map((card) => ({
            ...card,
            cardProduct: {
              ...card.cardProduct,
              cardProductImgUrl: card.cardProduct.cardProductImgUrl.replace(
                /\s/g,
                "_"
              ), // 공백을 _로 변환
            },
          })),
        }),

      addCard: (card: Card) =>
        set((state) => ({
          cardList: [
            ...state.cardList,
            {
              ...card,
              cardProduct: {
                ...card.cardProduct,
                cardProductImgUrl: card.cardProduct.cardProductImgUrl.replace(
                  /\s/g,
                  "_"
                ), // 공백을 _로 변환
              },
            },
          ],
        })),

      removeCard: (index) =>
        set((state) => {
          const newCardList = [...state.cardList]; // Create a copy of the current cardList
          newCardList.splice(index, 1); // Remove the card at the specified index
          return { cardList: newCardList }; // Return the updated state
        }),

      clearCards: () => set(() => ({ cardList: [] })),

      // 추가된 메서드: cardNumber로 특정 카드를 찾는 함수
      getCardByNumber: (cardNumber: string) => {
        const { cardList } = get(); // 상태에서 cardList 가져오기
        return cardList.find((card) => card.cardNumber === cardNumber); // cardNumber로 찾기
      },
      getCardByUuid: (uuid: string) => {
        const { cardList } = get(); // 상태에서 cardList 가져오기
        return cardList.find((card) => card.uuid === uuid); // cardNumber로 찾기
      },
      getCategoryImage: (categoryId: string) => {
        return categoryImages[categoryId]
          ? `/assets/image/category/${categoryImages[categoryId]}`
          : `/assets/image/category/ALL.png`; // 기본 이미지
      },
    }),
    {
      name: "card-storage", // localStorage에 저장될 키 이름
      getStorage: () => localStorage, // 기본적으로 localStorage에 저장
    }
  )
);
