const PATH = {
  ROOT: "/",
  HOME: "/home",
  BIOMETRICS_LOGIN: "/biometrics-login", //nonav
  SETTING_BIOMETRICS_LOGIN: "/setting-biometrics-login",
  CREATE_ACCOUNT: "/create-account", //nonav
  PASSWORD_LOGIN: "/password-login", //nonav
  CARD_RECOMMEND: "/card-recommend",
  STATISTICS: "/statistics/",
  SETTING: "/setting",
  PAYMENT_RECOMMENDATION: "/payment-recommendation", //nonav
  STATISTICS_SAVING: "saving",
  STATISTICS_ANALYSIS: "analysis",
  STATISTICS_BENEFITS: "benefits",
  STATISTICS_CONSUMPTION: "consumption",
  USER_CARD_LIST: "/card-list", //nonav
  // USER_CARD_DETAIL: "/card", //nonav
  USER_CARD_DETAIL: "/card/:id", //nonav
  DUTCHPAY: "/dutchpay", //nonav
  DUTCHINVITE:
    "/dutchpay/invite/:orderId/:totalPrice/:categoryId/:merchantId/:requestId/:maxMember/:room_id", //nonav
  DUTCHPARTICIPATION: "/dutchpay/participation", //nonav
  BRING_CARD: "/bring-card",
  SELECT_TYPE: "/select-type",
  PAYMENT: "/payment", //nonav
  DUTCHOPEN: "/dutch-open", //nonav
  SELECT_PAYMENT_TYPE: "/select-payment-type",
  ADD_CARD: "/add-card",
  DUTCH_RESULT: "/dutch-result/:roomId",
} as const;

export { PATH };
