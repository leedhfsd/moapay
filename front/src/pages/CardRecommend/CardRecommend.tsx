import { useEffect, useState } from "react";
import {
  Wrapper,
  Loading,
  Layout,
  CardViewContainer,
  CardView,
  ComparisonView,
  ComparisonList,
  Toggle,
  RecommendResult,
  RecommeCardListView,
  Title,
  RecommendResultCardList,
} from "./CardRecommend.styles";
import CardList from "../../components/CardRecommend/CardList/CardList";
import { MyCardList, RecommendedCardList } from "../../constants/card";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCaretDown,
  faCaretUp,
  faX,
  faCreditCard,
} from "@fortawesome/free-solid-svg-icons";
import { Card, CardProduct, useCardStore } from "../../store/CardStore";
import axios from "axios";
import { useAuthStore } from "../../store/AuthStore";
import ParticleCanvas from "../../components/ParticleCanvas";
import CardLoading from "./CardLoading";
import apiClient from "../../axios";

/**
 * 전부  CardProduct 데이터 형식을 가진 list로 사용
 * 1. 사용자 정보를 userCardProductList에 넣기 - 완료
 * 2. 추천 카드를 recommendCardList - 완료
 * 3. 클릭 시 비교 comparisonCard에 넣기
 */
const CardRecommend = () => {
  const [viewDetail, setViewDeatil] = useState<boolean>(false);
  const [compare, setCompare] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { id, accessToken, name } = useAuthStore();
  const { cardList, recommendCardList, setRecommendCardList } = useCardStore();
  const [userCardProductList, setUserCardProductList] = useState<CardProduct[]>(
    cardList.map((card) => card.cardProduct)
  );

  //비교공간
  const [comparisonCard, setComparisonCard] = useState<(CardProduct | null)[]>([
    null,
    null,
  ]);
  // 비교 모달 열고 닫기
  const [showComparisonView, setShowComparisonView] = useState<boolean>(false);
  //내카드 파트로 이동여부
  const [showUserCard, setShowUserCard] = useState<boolean>(false);
  //네브 위치
  const [navPosition, setNavPosition] = useState<string>(
    `calc(calc(100% / 2) * 0)`
  );

  const controllDetailView = (state: boolean) => {
    setViewDeatil(state);
  };
  const changeShowComparisonViewState = () => {
    setShowComparisonView((current) => !current);
  };

  const onCardClick = (card: CardProduct) => {
    console.log(card);
    setComparisonCard((prev) => {
      if (prev.length >= 2 && prev[0] && prev[1]) return prev; // 이미 2개의 카드가 선택되었으면 더 추가하지 않음
      //0이 비어 있을 때
      if (!prev[0]) {
        return [card, prev[1]];
      }
      //1이 비어 있을 때
      else {
        return [prev[0], card];
      }
    });
  };

  const deleteComparisonCard = (index: number) => {
    setComparisonCard((prev) => {
      if (index == 0) return [null, prev[1]];
      else return [prev[0], null];
    });
  };

  const getRecommendCards = async () => {
    try {
      const response = await apiClient.get(
        // `https://j11c201.p.ssafy.io/api/moapay/core/card/recommend/${id}`,
        `/api/moapay/core/card/recommend/${id}`,
        // `http://localhost:8765/moapay/core/card/recommend/${id}``,
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (response.status === 200) {
        const recommendedCards = response.data.data.recommend;

        // cardList에 있는 카드들의 cardProductName을 추출
        const cardNamesInMyList = cardList.map(
          (card: Card) => card.cardProduct.cardProductName
        );

        // recommendCardList에서 cardList에 없는 카드들만 필터링
        const filteredRecommendCards = recommendedCards.filter(
          (card: CardProduct) =>
            !cardNamesInMyList.includes(card.cardProductName)
        );

        setRecommendCardList(filteredRecommendCards);
      }
    } catch (e) {
      console.log(e);
    }
  };
  useEffect(() => {
    setIsLoading(true);
    getRecommendCards();
    setTimeout(() => {
      setIsLoading(false);
    }, 2000);
  }, []);

  return (
    <Wrapper>
      {isLoading ? (
        <CardLoading />
      ) : !compare ? (
        <>
          <RecommendResult>
            <ParticleCanvas />
            {/* <div className="not-layout"> */}
            <Title>
              <div>{name}님을 위한 추천 카드</div>

              <p>밀어서 카드 상세 정보를 볼 수 있어요</p>
            </Title>
            <RecommeCardListView>
              <RecommendResultCardList
                controllDetailView={controllDetailView}
                onCardClick={onCardClick}
                cardList={recommendCardList}
              />
            </RecommeCardListView>
            <button
              style={{ zIndex: viewDetail ? 0 : 10 }}
              onClick={() => {
                setCompare(true);
              }}
            >
              카드 비교하러 가기
            </button>
            {/* </div> */}
          </RecommendResult>
        </>
      ) : (
        <>
          <div className="view">
            <div className="top">
              <CardViewContainer>
                <CardView>
                  <div className="cardimg">
                    {comparisonCard[0] ? (
                      <>
                        <img
                          src={`/assets/image/longWidth/신용카드이미지/${comparisonCard[0].cardProductImgUrl}.png`}
                          alt={comparisonCard[0].cardProductName}
                        />
                        <div
                          className="delete-btn"
                          onClick={() => {
                            deleteComparisonCard(0);
                          }}
                        >
                          <FontAwesomeIcon icon={faX} />
                        </div>
                      </>
                    ) : (
                      <img
                        src="\assets\image\recommendedcard.png"
                        style={{ height: "100%" }}
                      />
                    )}
                  </div>
                  <div>vs</div>
                  <div className="cardimg">
                    {comparisonCard[1] ? (
                      <>
                        <img
                          src={`/assets/image/longWidth/신용카드이미지/${comparisonCard[1].cardProductImgUrl}.png`}
                          alt={comparisonCard[1].cardProductName}
                        />
                        <div
                          className="delete-btn"
                          onClick={() => {
                            deleteComparisonCard(1);
                          }}
                        >
                          <FontAwesomeIcon icon={faX} />
                        </div>
                      </>
                    ) : (
                      <img
                        style={{ height: "100%" }}
                        src="\assets\image\recommendedcard.png"
                      />
                    )}
                  </div>
                </CardView>
              </CardViewContainer>
              <ComparisonView>
                <div
                  onClick={changeShowComparisonViewState}
                  className="comparison-view-header"
                >
                  <p>
                    {showComparisonView ? "비교 결과 닫기" : "비교 결과 펼치기"}
                  </p>
                  {showComparisonView ? (
                    <FontAwesomeIcon icon={faCaretUp} />
                  ) : (
                    <FontAwesomeIcon icon={faCaretDown} />
                  )}
                </div>
                {showComparisonView ? (
                  <ComparisonList>
                    {comparisonCard[0] || comparisonCard[1] ? (
                      <>
                        <header>카드종류</header>
                        <div className="row">
                          <p>
                            {comparisonCard[0]
                              ? comparisonCard[0].cardProductType == "cred"
                                ? "신용카드"
                                : "체크카드"
                              : ""}
                          </p>
                          <div className="line"></div>
                          <p>
                            {comparisonCard[1]
                              ? comparisonCard[1].cardProductType == "cred"
                                ? "신용카드"
                                : "체크카드"
                              : ""}
                          </p>
                        </div>
                        <header>연회비</header>
                        <div className="row">
                          <p>
                            {comparisonCard[0]
                              ? comparisonCard[0].cardProductAnnualFee !== 0
                                ? `${comparisonCard[0].cardProductAnnualFee}원`
                                : "연회비 없음"
                              : ""}
                          </p>
                          <div className="line"></div>
                          <p>
                            {comparisonCard[1]
                              ? comparisonCard[1].cardProductAnnualFee !== 0
                                ? `${comparisonCard[1].cardProductAnnualFee}원`
                                : "연회비 없음"
                              : ""}
                          </p>
                        </div>
                        <header>전월실적</header>
                        <div className="row">
                          <p>
                            {comparisonCard[0]
                              ? comparisonCard[0].cardProductPerformance !== 0
                                ? `${comparisonCard[0].cardProductPerformance.toLocaleString()}원`
                                : "전월실적 없음"
                              : ""}
                          </p>
                          <div className="line"></div>
                          <p>
                            {comparisonCard[1]
                              ? comparisonCard[1].cardProductPerformance !== 0
                                ? `${comparisonCard[1].cardProductPerformance.toLocaleString()}원`
                                : "전월실적 없음"
                              : ""}
                          </p>
                        </div>
                        <header>주요혜택</header>
                        <div className="row benefit">
                          <p className="benefit-row">
                            {comparisonCard[0]
                              ? comparisonCard[0].cardBenefits?.map(
                                  (benefit, index) => (
                                    <>
                                      <p
                                        className="benefit-category"
                                        key={index}
                                      >
                                        {benefit.categoryName}
                                      </p>
                                      <p className="benefit-explanation">
                                        {benefit.benefitDesc}
                                      </p>
                                    </>
                                  )
                                )
                              : ""}
                          </p>
                          <div
                            style={{
                              width: "1px",
                              backgroundColor: "#a097ff",
                            }}
                          ></div>
                          <p className="benefit-row">
                            {comparisonCard[1]
                              ? comparisonCard[1].cardBenefits?.map(
                                  (benefit, index) => (
                                    <>
                                      <p
                                        className="benefit-category"
                                        key={index}
                                      >
                                        {benefit.categoryName}
                                      </p>
                                      <p className="benefit-explanation">
                                        {benefit.benefitDesc}
                                      </p>
                                    </>
                                  )
                                )
                              : ""}
                          </p>
                        </div>
                      </>
                    ) : (
                      <div className="notify">비교할 카드를 골라주세요!</div>
                    )}
                  </ComparisonList>
                ) : null}
              </ComparisonView>
            </div>
            <div className="bottom">
              <Toggle>
                <p
                  onClick={() => {
                    setNavPosition(`calc(calc(100% / 2) *0)`);
                    setShowUserCard(false);
                  }}
                >
                  추천카드
                </p>
                <p
                  onClick={() => {
                    setNavPosition(`calc(calc(100% / 2) * 1)`);
                    setShowUserCard(true);
                  }}
                >
                  내카드
                </p>
                <div style={{ left: `${navPosition}` }}></div>
              </Toggle>
              <div className="slide-notify-parent">
                <div className="slide-notify">밀어서 상세보기</div>
              </div>
              <CardList
                controllDetailView={controllDetailView}
                onCardClick={onCardClick}
                cardList={
                  showUserCard ? userCardProductList : recommendCardList
                }
              />
            </div>
          </div>
        </>
      )}
    </Wrapper>
  );
};

export default CardRecommend;
