import { ToggleWrapper,Input,Label } from "./Toggle.styles"

type Props = {
    consumptionMode :boolean;
    handleToggle:()=>void;
}

const Toggle:React.FC<Props>= ({consumptionMode,handleToggle}) =>{

    return(
              <ToggleWrapper>
        <div className="toggle normal">
          <Input
            type="checkbox"
            id="normal"
            checked={consumptionMode}
            onChange={handleToggle}
          />
         <Label htmlFor="normal" checked={consumptionMode}>
            {/* <p>{consumptionMode ? "소비" : "혜택"}</p> */}
          </Label>
        </div>
      </ToggleWrapper>
      )
}
export default Toggle;