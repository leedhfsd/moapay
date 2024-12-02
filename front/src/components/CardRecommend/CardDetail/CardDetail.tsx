import { useState } from "react";
import { Benefits, CardInfoRow, Wrapper } from "./CardDetail.styles";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { Card, CardProduct } from "../../../store/CardStore";

interface Props {
  selectedCard: CardProduct | null;
  closeCardDetail: () => void;
}
const CardDetail = ({ selectedCard, closeCardDetail }: Props) => {
  // 이미지 로드 시 회전 여부 설정

  return (
    <Wrapper>
      <div className="delete-btn" onClick={closeCardDetail}>
        <FontAwesomeIcon
          style={{ width: "100%", height: "100%" }}
          icon={faXmark}
        />
      </div>
      <h1>{selectedCard?.cardProductName}</h1>
      <img
        src={`/assets/image/longWidth/신용카드이미지/${selectedCard?.cardProductImgUrl}.png`}
      />
      <CardInfoRow>
        <header style={{ marginTop: "78px" }}>
          <div className="line"></div>
          <span>카드종류</span>
          <div className="line"></div>
        </header>
        <div className="value">
          {selectedCard?.cardProductType == "cred" ? "체크카드" : "신용카드"}
        </div>
      </CardInfoRow>
      <CardInfoRow>
        <header>
          <div className="line"></div>
          <span>연회비</span>
          <div className="line"></div>
        </header>
        <div className="value">
          {selectedCard?.cardProductAnnualFee !== 0
            ? `${selectedCard?.cardProductAnnualFee}원`
            : "연회비 없음"}
        </div>
      </CardInfoRow>
      <CardInfoRow>
        <header>
          <div className="line"></div>
          <span>전월실적</span>
          <div className="line"></div>
        </header>
        <div className="value">
          {selectedCard?.cardProductPerformance !== 0
            ? `${selectedCard?.cardProductPerformance}원`
            : "전월실적 없음"}
        </div>
      </CardInfoRow>
      <Benefits>
        <header>
          <div className="line"></div>
          <span>혜택상세</span>
          <div className="line"></div>
        </header>
        <ul>
          {selectedCard?.cardBenefits?.map((benefit) => (
            <div>
              <p></p>
              <li>{benefit.benefitDesc}</li>
            </div>
          ))}
        </ul>
      </Benefits>
    </Wrapper>
  );
};

export default CardDetail;
