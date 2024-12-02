import { useState } from "react";
import { CardInfo } from "./CardList.styles";
import CardDetail from "../CardDetail/CardDetail";
import { Card, CardProduct } from "../../../store/CardStore";

interface Props {
  cardList: CardProduct[];
  onCardClick: (card: CardProduct) => void;
  controllDetailView: (state: boolean) => void;
}

const CardList = ({ cardList, onCardClick, controllDetailView }: Props) => {
  const [translateX, setTranslateX] = useState<{ [key: number]: number }>({});
  const [startX, setStartX] = useState<number | null>(null); // 터치 시작 위치 저장
  const [showCardDetail, setShowCardDetail] = useState<boolean>(false); // 새로운 화면 표시 여부
  const [selectedCard, setSelectedCard] = useState<CardProduct | null>(null);
  const [animationExecuted, setAnimationExecuted] = useState<boolean>(false); // 애니메이션이 실행된 상태 추적

  const handleTouchStart = (e: React.TouchEvent, index: number) => {
    setStartX(e.touches[0].clientX); // 터치 시작 X 좌표 기록
  };

  const handleTouchMove = (e: React.TouchEvent, index: number) => {
    if (startX !== null) {
      const currentX = e.touches[0].clientX;
      const diffX = startX - currentX; // 터치 이동 거리 계산 (오른쪽에서 왼쪽으로 이동)
      if (Math.abs(diffX - translateX[index]) > 5) {
        setTranslateX((prev) => ({ ...prev, [index]: diffX }));
      }
    }
  };

  const handleTouchEnd = (e: React.TouchEvent, index: number) => {
    if (startX !== null) {
      const endX = e.changedTouches[0].clientX;
      const diffX = startX - endX; // 실제 이동 거리 계산
      if (Math.abs(diffX) > 200) {
        // 이동 거리가 300px 이상일 때만 새로운 화면을 표시
        controllDetailView(true);
        setShowCardDetail(true);
        setSelectedCard(cardList[index]);
      }
      // 카드가 원래 위치로 복귀
      setTranslateX((prev) => ({ ...prev, [index]: 0 }));
    }

    // 터치 시작 위치 초기화
    setStartX(null);
  };

  const handleAnimationEnd = () => {
    // 애니메이션이 끝나면 상태를 업데이트하여 재실행 방지
    setAnimationExecuted(true);
  };

  const closeCardDetail = () => {
    setShowCardDetail(false);
    controllDetailView(false);
  };

  return (
    <>
      {cardList.map((card, index) => (
        <CardInfo
          onTouchStart={(e) => handleTouchStart(e, index)}
          onTouchMove={(e) => handleTouchMove(e, index)}
          onTouchEnd={(e) => handleTouchEnd(e, index)}
          onAnimationEnd={handleAnimationEnd} // 애니메이션이 끝났을 때 상태 업데이트
          style={{
            transform: `translateX(-${translateX[index] || 0}px)`, // 오른쪽에서 왼쪽으로 이동
            // transition: "transform 0.3s ease", // 부드러운 이동 애니메이션
            willChange: "transform",
          }}
          onClick={() => onCardClick(card)}
          key={index}
        >
          <div
            className={
              index === 0 && !animationExecuted
                ? "active row" // 인덱스가 0이고 애니메이션이 실행되지 않은 경우에만 active
                : "row"
            }
          >
            <div>
              <img
                src={`/assets/image/longWidth/신용카드이미지/${card?.cardProductImgUrl}.png`}
                alt={card?.cardProductImgUrl}
              />
            </div>
            <h3>{card.cardProductName}</h3> {/* 카드명 */}
          </div>
        </CardInfo>
      ))}

      {/* 새로운 화면 표시 */}
      {showCardDetail && (
        <CardDetail
          selectedCard={selectedCard}
          closeCardDetail={closeCardDetail}
        />
      )}
    </>
  );
};

export default CardList;
