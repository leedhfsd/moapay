import styled from "styled-components"

export const Wrapper = styled.div`
  height: 100%;
  width: 100%;
  position: relative;

  & > img {
    padding: 15px 0;
  }
`

export const Price = styled.div`
  color: #8748F3;
  text-align: center;
  padding: 20px 0 5px 0;
  font-size: 18px;
  font-weight: 700;
`

export const Title = styled.div`
  font-weight: 700;
  font-size: 24px;
  text-align: center;
`
export const PartiList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-top: 15px;
`

export const PartiInfo = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  & > input {
    width: 28%;
    text-align: center;
    background-color: rgba(197, 202, 255, 0.44);
    border: 0;
    border-radius: 5px;
    padding: 3px 0;
  }

  & > div:nth-of-type(2) {
    font-size: 20px;
  }
    
`

export const WarningMessage = styled.div`
  color: #D60000;
  position: absolute;
  width: 100%;
  bottom: 5px;
  // left: 2.5%;
  text-align: center;
`

export const Btn = styled.div`
  position: absolute;
  width: 100%;
  bottom: 30px;
  left: 2.5%;
  
  // margin: 0 auto;
`