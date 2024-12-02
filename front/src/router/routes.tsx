// router.tsx
import { createBrowserRouter } from "react-router-dom";
import { PATH } from "../constants/path";
import ProtectedRoute from "../pages/ProtectedRoute";
import Layout from "../components/layout/Layout/Layout";
import BiometricsLogin from "../pages/BiometricsLogin/BiometricsLogin";
import CreateAccount from "../pages/CreateAccount/CreateAccount";
import Home from "../pages/Home/Home.tsx";
import AppAuthHandler from "../pages/AppAuthHandler";
import PasswordLogin from "../pages/PasswordLogin/PasswordLogin.tsx";
import CardRecommend from "../pages/CardRecommend/CardRecommend.tsx";
import Setting from "../pages/Setting/Setting.tsx";
import Statistics from "../pages/Statistics/Statistics.tsx";
import Saving from "../pages/Statistics/Saving/Saving.tsx";
import Analysis from "../pages/Statistics/Analysis/Analysis.tsx";
import Benefits from "../pages/Statistics/Benefits/Benefits.tsx";
import Consumption from "../pages/Statistics/Consumption/Consumption.tsx";
import PaymentRecommendation from "../pages/PaymentRecommendation/PaymentRecommendation.tsx";
import UserCardDetail from "../pages/UserCardDetail/UserCardDetail.tsx";
import SettingBiometricsLogin from "../pages/SettingBiometricsLogin/SettingBiometricsLogin.tsx";
import UserCardList from "../pages/UserCardList/UserCardList.tsx";
import Dutchpay from "../pages/Dutchpay/Dutchpay.tsx";
import DutchInvite from "../pages/DutchInvite/DutchInvite.tsx";
import DutchParticipation from "../pages/DutchParticipation/DutchParticipation.tsx";
import BringCard from "../pages/BringCard/BringCard.tsx";
import SelectType from "../pages/SelectType/SelectType.tsx";
import { elements } from "chart.js";
import RegisterCard from "../pages/RegisterCard/RegisterCard.tsx";
import Payment from "../pages/Payment/Payment.tsx";
import SelectPaymentType from "../pages/SelectPaymentType/SelectPaymentType.tsx";
import DutchOpen from "../pages/DutchOpen/DutchOpen.tsx";
import DutchResult from "../pages/DutchResult/DutchResult.tsx";
const router = createBrowserRouter([
  {
    element: <AppAuthHandler />, // 최상위 레이아웃으로 AppAuthHandler 설정
    children: [
      {
        path: PATH.ROOT,
        element: (
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        ),
        children: [
          {
            path: PATH.HOME,
            element: <Home />,
          },
          {
            path: PATH.CARD_RECOMMEND,
            element: <CardRecommend />,
          },
          {
            path: PATH.SETTING,
            element: <Setting />,
          },
          {
            path: PATH.STATISTICS,
            element: <Statistics />,
            children: [
              {
                path: PATH.STATISTICS_SAVING,
                element: <Saving />,
              },
              {
                path: PATH.STATISTICS_ANALYSIS,
                element: <Analysis />,
              },
              {
                path: PATH.STATISTICS_BENEFITS,
                element: <Benefits />,
              },
              {
                path: PATH.STATISTICS_CONSUMPTION,
                element: <Consumption />,
              },
            ],
          },
          {
            path: PATH.PAYMENT_RECOMMENDATION,
            element: <PaymentRecommendation />,
          },
          {
            path: PATH.USER_CARD_LIST,
            element: <UserCardList />,
          },
          {
            path: PATH.USER_CARD_DETAIL,
            element: <UserCardDetail />,
          },
          {
            path: PATH.BRING_CARD,
            element: <BringCard />,
          },
          {
            path: PATH.SELECT_TYPE,
            element: <SelectType />,
          },
          { path: PATH.ADD_CARD, element: <RegisterCard /> },
          {
            path: PATH.SELECT_PAYMENT_TYPE,
            element: <SelectPaymentType />,
          },
          {
            path: PATH.DUTCHPAY,
            element: <Dutchpay />,
          },
          {
            path: PATH.DUTCHINVITE,
            element: <DutchInvite />,
          },
          {
            path: PATH.DUTCHPARTICIPATION,
            element: <DutchParticipation />,
          },
          {
            path: PATH.PAYMENT,
            element: <Payment />,
          },
          {
            path: PATH.DUTCHOPEN,
            element: <DutchOpen />,
          },
          {
            path: PATH.DUTCH_RESULT,
            element: <DutchResult />,
          },
        ],
      },
      {
        path: PATH.BIOMETRICS_LOGIN,
        element: <BiometricsLogin />,
      },
      {
        path: PATH.CREATE_ACCOUNT,
        element: <CreateAccount />,
      },
      { path: PATH.PASSWORD_LOGIN, element: <PasswordLogin /> },
      {
        path: PATH.SETTING_BIOMETRICS_LOGIN,
        element: <SettingBiometricsLogin />,
      },
    ],
  },
]);

export default router;
