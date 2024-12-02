import {
  Btn
} from './SquareBtn.styles'

interface SquareBtnProps {
  text: string;
  color: string;
  onClick: () => void;
}

const SquareBtn = ({text, color='rgba(135, 72, 243, 0.74)', onClick}:SquareBtnProps) => {
  return (
    <Btn
      style={{
        backgroundColor: color
      }}
      onClick={onClick}
    >
      {text}
    </Btn>
  )
}

export default SquareBtn;