import { useRef, useEffect } from "react";
import {
  Wrapper,
  Log,
  Content,
  Date,
  DetailLeft,
  Detail,
  Price,
  EmptyLogs,
} from "./DetailPayLogList.styles";
import { payLog, useCardStore } from "../../../store/CardStore"; // payLog 인터페이스 가져오기

interface DetailPayLogListProps {
  payLogList: payLog[]; // 결제 로그 배열로 타입 지정
}

const DetailPayLogList = ({ payLogList = [] }: DetailPayLogListProps) => {
  const imgRefs = useRef<{ [key: number]: HTMLImageElement | null }>({}); // 여러 이미지를 객체로 참조하는 방식

  const { getCategoryImage } = useCardStore();

  // 이미지 크기 조정 로직
  useEffect(() => {
    Object.values(imgRefs.current).forEach((imgElement) => {
      if (imgElement) {
        const naturalWidth = imgElement.naturalWidth;
        const naturalHeight = imgElement.naturalHeight;

        if (naturalWidth > 50 || naturalHeight > 50) {
          imgElement.style.width = "60%";
          imgElement.style.height = "auto"; // 비율 유지
        } else {
          imgElement.style.height = "60%";
          imgElement.style.width = "auto"; // 비율 유지
        }
      }
    });
  }, [payLogList]);
  // 날짜별로 그룹화하는 함수
  const groupByDate = (payLogList: payLog[]) => {
    return payLogList.reduce(
      (acc: { [key: string]: payLog[] }, logItem: payLog) => {
        const date = logItem.createTime.split("T")[0]; // "YYYY-MM-DD" 추출
        if (!acc[date]) {
          acc[date] = [];
        }
        acc[date].push(logItem); // 동일한 날짜끼리 그룹화
        return acc;
      },
      {} as { [key: string]: payLog[] }
    );
  };

  const groupedLogs = groupByDate(payLogList); // 그룹화된 로그
  // const groupedLogs = {
  //   "2024-10-14": [
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 15000,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T07:01:11.050916",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 1130000,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T06:59:24.738039",
  //     },
  //     {
  //       merchantName: "삼성전자",
  //       amount: 11300,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T05:41:16.010412",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 11300,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T04:56:21.876093",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 753333,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T04:42:00.861026",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 1130000,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T04:26:27.786181",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 1130000,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T04:26:27.786181",
  //     },
  //     {
  //       merchantName: "삼성전자온라인쇼핑몰",
  //       amount: 1130000,
  //       benefitBalance: 0,
  //       categoryId: "C0008",
  //       createTime: "2024-10-14T04:26:27.786181",
  //     },
  //   ],
  // };
  console.log("===============", groupedLogs, "==============="); // 콘솔에서 확인

  return (
    <Wrapper>
      {Object.keys(groupedLogs).length !== 0 ? (
        <>
          {Object.keys(groupedLogs).map((date, index) => {
            const [year, month, day] = date.split("-"); // "YYYY-MM-DD"에서 월과 일 추출
            return (
              <Log key={index}>
                <Date>
                  {parseInt(month, 10)}월 {parseInt(day, 10)}일
                </Date>
                {groupedLogs[date].map((logItem: payLog, logIndex: number) => {
                  const refIndex = index * 100 + logIndex; // 고유한 인덱스 생성
                  return (
                    <Content key={logIndex}>
                      <div>
                        <DetailLeft>
                          <div style={{ backgroundColor: "white" }}>
                            <img
                              ref={(el) => (imgRefs.current[refIndex] = el)}
                              src={getCategoryImage(logItem.categoryId)}
                              alt=""
                              style={{ width: "50px", height: "50px" }} // 이미지 크기 조정
                            />
                          </div>
                          <Detail>
                            <div
                              style={{
                                marginBottom: 10,
                                fontSize: 18,
                                fontWeight: 700,
                              }}
                            >
                              {logItem.merchantName}
                            </div>
                            <div>
                              {logItem.createTime.split("T")[1].slice(0, 5)}
                            </div>
                          </Detail>
                        </DetailLeft>
                        <Price>
                          <div>{logItem.amount.toLocaleString()}원</div>
                        </Price>
                      </div>
                    </Content>
                  );
                })}
              </Log>
            );
          })}
        </>
      ) : (
        <EmptyLogs>
          <img src="/assets/image/nopaycolor.png" />
          <div>결제 내역이 없어요.</div>
        </EmptyLogs>
      )}
    </Wrapper>
  );
};

export default DetailPayLogList;
