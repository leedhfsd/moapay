import { useEffect, useRef } from "react";
import { Wrapper, ListItem } from "./List.styles";
import { categoryData, useCardStore } from "../../../store/CardStore";

interface Props {
  consumptionList: categoryData[]; // props로 받을 데이터 타입 지정
}

const List = ({ consumptionList }: Props) => {
  const imgRefs = useRef<(HTMLImageElement | null)[]>([]); // 여러 이미지 요소 참조

  console.log("마지막 위치", consumptionList);
  const { getCategoryImage } = useCardStore();
  useEffect(() => {
    imgRefs.current.forEach((imgElement) => {
      if (imgElement) {
        // 이미지의 너비와 높이가 50px을 넘으면 width를 60%로 설정
        if (imgElement.naturalWidth > 50 || imgElement.naturalHeight > 50) {
          imgElement.style.width = "60%";
          imgElement.style.width = "55%";
        } else {
          imgElement.style.height = "60%";
          imgElement.style.height = "55%";
        }
      }
    });
  }, [consumptionList]); // consumptionList 변경 시마다 이미지 크기 다시 체크

  // per 기준으로 내림차순 정렬
  const sortedList = [...consumptionList].sort((a, b) => b.per - a.per);

  return (
    <Wrapper className="list-wrapper">
      {sortedList.map((consumption: categoryData, index: number) => (
        <ListItem key={index}>
          <div className="Col">
            <div>
              {consumption.categoryId === "ALL" ? (
                <p style={{ color: "pink", fontWeight: "600" }}>ALL</p>
              ) : (
                <img
                  ref={(el) => (imgRefs.current[index] = el)} // 각 이미지를 참조 배열에 저장
                  src={getCategoryImage(consumption.categoryId)}
                  alt={consumption.categoryId}
                />
              )}
            </div>
          </div>
          <div className="Col">
            <p>
              {getCategoryImage(consumption.categoryId)
                .replace("/assets/image/category/", "")
                .slice(0, -4)}
            </p>
            <p>
              {`${consumption.per}%`} | {consumption.money.toLocaleString()}원
            </p>
          </div>
        </ListItem>
      ))}
      <div className="last-box"></div>
    </Wrapper>
  );
};

export default List;
