import React, { useEffect, useState, useRef } from "react";
import {
  Chart as ChartJS,
  BarElement,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  LineController,
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
  LineController, // Line 차트 컨트롤러
  LineElement,
  CategoryScale,
  LinearScale,
  Title,
  Tooltip,
  Legend,
  PointElement
);

type Props = {
  userDataList: number[] | null;
  avgDataList: number[] | null;
  consumptionMode: boolean;
};

const MixedChart: React.FC<Props> = ({
  consumptionMode,
  avgDataList,
  userDataList,
}) => {
  const [months, setMonths] = useState<string[]>([]);
  const chartRef = useRef<any>(null);
  const [xRange, setXRange] = useState<{ min: number; max: number }>({
    min: 6,
    max: 11,
  });

  const chartTitle: string = consumptionMode ? "소비 그래프" : "혜택 그래프";
  const backgroundColor: string = consumptionMode ? "#FFAAE7" : "#D7D9FF";

  useEffect(() => {
    const getLast12Months = () => {
      const result: string[] = [];
      const now = new Date();

      for (let i = 0; i < 12; i++) {
        const year = now.getFullYear();
        const month = now.getMonth() + 1;
        result.push(`${year}.${month < 10 ? `0${month}` : month}`);

        now.setMonth(now.getMonth() - 1);
      }

      return result.reverse();
    };

    setMonths(getLast12Months());
  }, []);

  /**문제된 곳 */
  const data: ChartData<"line" | "bar", number[] | null, string> = {
    labels: months,
    datasets: [
      {
        type: "line",
        data: avgDataList,
        borderColor: "rgba(153, 102, 255, 1)",
        borderWidth: 2,
        fill: false,
      },
      {
        type: "bar",
        data: userDataList,
        backgroundColor: backgroundColor,
      },
    ],
  };

  const options: ChartOptions<"line" | "bar"> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        min: xRange.min,
        max: xRange.max,
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: chartTitle,
        font: {
          size: 18,
        },
        align: "center", // 수정된 부분: "center" 대신 'center'
      },
    },
  };

  let touchStartX = 0;

  const handleTouchStart = (event: React.TouchEvent<HTMLDivElement>) => {
    touchStartX = event.touches[0].clientX;
  };

  const handleTouchMove = (event: React.TouchEvent<HTMLDivElement>) => {
    const touchEndX = event.touches[0].clientX;
    if (touchEndX > 390 || touchStartX < 70) return;
    const deltaX = Math.trunc(Math.abs(touchEndX - touchStartX) / 6);
    setXRange((prevRange) => {
      let newMin, newMax;

      if (touchEndX - touchStartX < 0) {
        newMin = Math.min(prevRange.min + deltaX, 6);
        newMax = Math.min(prevRange.max + deltaX, 11);
      } else {
        newMin = Math.max(prevRange.min - deltaX, 0);
        newMax = Math.max(prevRange.max - deltaX, 5);
      }

      if (newMin !== prevRange.min || newMax !== prevRange.max) {
        return { min: newMin, max: newMax };
      }

      return prevRange;
    });
  };

  return (
    <div
      style={{ width: "100%", height: "100%", overflowX: "hidden" }}
      onTouchStart={handleTouchStart}
      onTouchMove={handleTouchMove}
    >
      <Chart
        ref={chartRef}
        style={{ height: "100%" }}
        type="bar"
        data={data}
        options={options}
      />
    </div>
  );
};

export default MixedChart;
