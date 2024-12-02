import { useState } from "react";

// import testcard1 from "./../../../assets/image/cards/신용카드이미지/14_JADE_Classic.png";
// import testcard2 from "./../../../assets/image/cards/신용카드이미지/12_올바른_FLEX_카드.png";
// import testcard3 from "./../../../assets/image/cards/신용카드이미지/11_삼성_iD_SIMPLE_카드.png";

import { Wrapper, CardView } from "./SelectCardList.styles";
import { Card } from "../../../store/CardStore";
import { useCardStore } from "../../../store/CardStore";
// import { MyCardList } from "../../../constants/card";

interface SelectCardListProps {
  onSelectCard: (card: Card) => void;
}
const SelectCardList: React.FC<SelectCardListProps> = ({ onSelectCard }) => {
  const { cardList } = useCardStore();
  const [rotate, setRotate] = useState<{ [key: number]: boolean }>({});

  // 카드 가로, 세로 길이에 따른 회전 여부 판단 핸들러
  const handleImageLoad = (
    event: React.SyntheticEvent<HTMLImageElement, Event>,
    index: number
  ) => {
    const imgElement = event.currentTarget;
    if (imgElement.naturalWidth < imgElement.naturalHeight) {
      setRotate((prevRotate) => ({ ...prevRotate, [index]: true }));
    }
  };

  return (
    <Wrapper>
      {cardList.map((card, index) => (
        <div key={index}>
          <CardView key={index} onClick={() => onSelectCard(card)}>
            <div>
              <img
                src={`/assets/image/longWidth/신용카드이미지/${card.cardProduct.cardProductImgUrl}.png`}
                // src={card.cardProduct.cardProductImgUrl}
                alt={card.cardProduct.cardProductName}
                onLoad={(event) => handleImageLoad(event, index)} // 이미지가 로드되면 handleImageLoad 호출
                style={{
                  position: "absolute",
                  // 19 30 38 60 9.5 15 47.5 75
                  width: rotate[index] ? "47.5px" : "75px", // 회전 여부에 따라 width와 height 변경
                  height: rotate[index] ? "75px" : "47.5px",
                  transform: rotate[index] ? "rotate(-90deg)" : "none", // 회전시키기
                  marginLeft: rotate[index] ? "14px" : "0",
                  userSelect: "none", // 드래그 방지
                }}
                draggable="false" // 이미지 드래그 방지
              />
              <div>
                <div>{card.cardProduct.cardProductName}</div>
                <div>
                  {`남은 실적 금액 : ${(
                    card.cardProduct.cardProductPerformance - card.amount
                  ).toLocaleString()}원`}
                </div>
              </div>
            </div>
          </CardView>
        </div>
      ))}
    </Wrapper>
  );
};

export default SelectCardList;
