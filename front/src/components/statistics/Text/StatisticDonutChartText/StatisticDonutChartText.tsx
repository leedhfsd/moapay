import { Chart } from "./StatisticDonutChartText.styles";

type TextProps = {
  text: string;
};

const StatisticDonutChartText: React.FC<TextProps> = ({ text }) => {
  return (
    <Chart>
      {text.split("\n").map((line, index) => (
        <p style={{}} key={index}>
          {index === 1
            ? new Intl.NumberFormat("ko-KR").format(
                Number(line.replace(/[^0-9]/g, ""))
              ) + "원"
            : line}
          <br />
        </p>
      ))}
    </Chart>
  );
};

export default StatisticDonutChartText;
