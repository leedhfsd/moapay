import React, { useRef } from "react";
import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  BarController,
  Title,
  Tooltip,
  Legend,
  ChartOptions,
  ChartData,
} from "chart.js";
import { Chart } from "react-chartjs-2";

ChartJS.register(
  BarElement,
  BarController, // Bar 차트 컨트롤러
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend
);

type Props = { consumptionList: number[] };

const SmallBarGraph: React.FC<Props> = ({ consumptionList }) => {
  const chartRef = useRef<any>(null);

  // 데이터 타입을 정확히 지정
  const data: ChartData<"bar", number[], string> = {
    labels: ["일", "월", "화", "수", "목", "금", "토"],
    datasets: [
      {
        type: "bar",
        data: consumptionList,
        backgroundColor: "rgba(159, 57, 248, 0.551)", // 바 차트의 배경색
        borderColor: "rgb(147, 24, 255)", // 바 차트의 테두리색
        borderWidth: 2,
      },
    ],
  };

  const options: ChartOptions<"bar"> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        beginAtZero: true,
      },
      y: {
        beginAtZero: true,
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: "",
        font: {
          size: 18,
        },
        align: "center",
      },
    },
  };

  return (
    <div style={{ width: "100%", height: "250px", overflowX: "hidden" }}>
      <Chart
        ref={chartRef}
        style={{ height: "250px", width: "100%" }}
        type="bar"
        data={data}
        options={options}
      />
    </div>
  );
};

export default SmallBarGraph;
