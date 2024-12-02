import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend, Title } from "chart.js";
import { Chart } from "./DonutChart.styles";
import { categoryData } from "../../../../store/CardStore";

// Chart.js에서 사용할 요소를 등록
ChartJS.register(ArcElement, Tooltip, Legend, Title);

interface Props {
  dataList: categoryData[] | null; // props로 받을 데이터 타입 지정
}
const DonutChart = ({ dataList }: Props) => {
  const sortedDataList = [...(dataList || [])].sort((a, b) => b.per - a.per);

  // 상위 5개의 데이터를 추출
  const top5Data = sortedDataList.slice(0, 5);

  // 나머지 데이터를 '기타'로 묶기
  const remainingData = sortedDataList.slice(5);

  // '기타' 항목의 money와 per 값을 계산
  const otherMoney = remainingData.reduce((sum, data) => sum + data.money, 0);
  const otherPer = 100 - top5Data.reduce((sum, data) => sum + data.per, 0);

  // 새로운 dataList를 생성
  const newDataList: categoryData[] = [
    ...top5Data,
    {
      categoryId: "기타",
      money: otherMoney,
      per: otherPer,
    },
  ];

  // sortedDataList에서 레이블과 데이터 추출
  const labels = newDataList.map((item) => item.categoryId);
  const values = newDataList.map((item) => item.money);
  const data = {
    labels: labels,
    datasets: [
      {
        data: values,
        backgroundColor: [
          "rgba(255, 99, 132, 0.85)",
          "rgba(255, 159, 64, 0.85)",
          "rgba(255, 205, 86, 0.85)",
          "rgba(75, 192, 192, 0.85)",
          "rgba(68,87,255,0.85)",
          "rgba(215,215,215,0.85)",
        ],
        borderColor: [
          "rgba(255, 99, 132, 0.85)",
          "rgba(255, 159, 64, 0.85)",
          "rgba(255, 205, 86, 0.85)",
          "rgba(75, 192, 192, 0.85)",
          "rgba(68,87,255,0.85)",
          "rgba(215,215,215,0.85)",
        ],
        borderWidth: 1,
      },
    ],
  };

  // 차트 옵션
  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: false, // 범례를 비활성화하여 상단 설명을 숨김
        // position: "top" as const, // 범례 위치 설정 (top, bottom, left, right)
      },
      // title: {
      //   display: true,
      //   text: "Sample Doughnut Chart", // 차트 제목
      //   font: {
      //     size: 18, // 제목의 폰트 크기
      //   },
      // },
    },
  };

  return (
    <Chart>
      <Doughnut data={data} options={options} />
    </Chart>
  );
};

export default DonutChart;
