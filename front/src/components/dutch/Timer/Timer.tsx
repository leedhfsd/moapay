import { memo } from "react";

const Timer = memo(({ timeLeft }: { timeLeft: number }) => {
  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes < 10 ? `0${minutes}` : minutes}:${
      seconds < 10 ? `0${seconds}` : seconds
    }`;
  };

  return (
    <div>
      {/* 타이머 값을 표시 */}
      {formatTime(timeLeft)}
    </div>
  );
});

export default Timer;