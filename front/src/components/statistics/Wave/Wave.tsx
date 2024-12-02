import { Layout, WaveDiv } from "./Wave.styles";
const Wave = () => {
  return (
    <Layout>
      <div className="first-wave"></div>
      <WaveDiv>
        <div className="second-wave"></div>
        <div className="last-wave"></div>
      </WaveDiv>
    </Layout>
  );
};
export default Wave;
