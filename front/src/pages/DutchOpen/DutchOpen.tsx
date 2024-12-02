import triangle from "./../../assets/image/triangle.png"
import Modal from "../../components/dutch/Modal/Modal";
import { useNavigate } from "react-router-dom";
import axios from "axios";
// import { Client } from "@stomp/stompjs";

// Dutchpay 페이지에서 상품 가격이 늦게 불러와진다면 해당 페이지에서 상품 정보 불러와서 가격만 넘겨주는 방식도 고려해보기

import {
  Wrapper,
  Top,
  Title,
  LinkBox,
  // CopyIcon,
  // ShareUrl,
  Main,
  BackImg,
} from './DutchOpen.styles'

import { useEffect, useState } from "react";
import { PATH } from "../../constants/path";
// import { useAuthStore } from "../../store/AuthStore";

const DutchOpen = () => {
  const nav = useNavigate()

  const [orderId, setOrderId] = useState("");
  const [totalPrice, setTotalPrice] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [merchantId, setMerchantId] = useState("");
  const [requestId, setRequestId] = useState("");
  // const { name, id } = useAuthStore()

  useEffect(() => {
    // localStorage에서 값 가져오기
    setOrderId(localStorage.getItem("orderId") || "");
    setTotalPrice(localStorage.getItem("totalPrice") || "");
    setCategoryId(localStorage.getItem("categoryId") || "");
    setMerchantId(localStorage.getItem("merchantId") || "");
    setRequestId(localStorage.getItem("requestId") || "");

    // 값 로그로 출력
    console.log("Order ID:", localStorage.getItem("orderId"));
    console.log("Total Price:", localStorage.getItem("totalPrice"));
    console.log("Category ID:", localStorage.getItem("categoryId"));
    console.log("Merchant ID:", localStorage.getItem("merchantId"));
    console.log("Request ID:", localStorage.getItem("requestId"));
  }, []);

  const [isOpen, setIsOpen] = useState<boolean>(false); // 더치페이 나가기 모달 상태 관리
  const [isCompleteSettingCheck, setIsCompleteSettingCheck] = useState<boolean>(false); // 더치페이 인원 설정 완료 확인용 모달 띄우기 판단용
  const [memberNum, setMemberNum] = useState<number | string>(''); // 참여자 수 입력 받는 변수
  
  // const [memberSetComplete, setMemberSetComplete] = useState<boolean>(false) // 참여자수 설정 완료 여부 판단용
  const [stop, setStop] = useState<boolean>(false) // 더치페이 중단하기 버튼을 눌렀는지의 여부를 판단
  
  
  console.log("멤버 수 변화 테스트용", memberNum)

  

  // // 참여자 수 바인딩
  // const onChangeMember = (e: React.ChangeEvent<HTMLInputElement>) => {
  //   console.log("확인용",e.target.value)
  //   setMemberNum(e.target.value)
  // }

  // 참여자 수 입력 후 완료 버튼을 눌렀을 경우=>설정 완료로 변경
  const onCheckComplete = () =>{
    localStorage.setItem('maxMember', String(memberNum)); // memberNum 값을 문자열로 저장
    console.log('로컬 스토리지 저장 확인', localStorage.getItem('maxMember'));
    setIsCompleteSettingCheck(true)
    nav(PATH.DUTCHPAY) // 더치페이 화면으로 이동
  }

  const goHome = () => {
    nav(PATH.HOME)
  }

  // 더치페이 나가기 버튼 클릭 시 모달 띄우기
  const openModal = () => {
    console.log("더치페이 나가기 버튼 클릭");
    setIsOpen(true);
  };

  const closeModal = () => {
    setIsOpen(false);
  };
  const closeSettingModal = () => {
    setIsCompleteSettingCheck(false);
  };
  const completeMemberSetting = () => {
    // setMemberSetComplete(true) // 나중에 지울 수도 있음
    localStorage.setItem('maxMember', String(memberNum)); // memberNum 값을 문자열로 저장
    console.log('로컬 스토리지 저장 확인', localStorage.getItem('maxMember'));
    setIsCompleteSettingCheck(false);
    nav(PATH.DUTCHPAY); // 더치페이 화면으로 이동


    // stomp 열기...? 더치페이 방 여는 axios 요청
  };

  // 더치페이 중단 버튼 클릭
  const onClickStop = () => {
    setStop(true)
  }

  // useEffect(() => {
  //   // localStorage.removeItem('maxMember');
  //   // setMemberSetComplete(false)
  // }, []) // 맨 처음 랜더링 시(더치페이를 처음으로 실행시킬 시) 값 초기화해야할 것들

  return (
    <Wrapper>
      <Top>
        <Title>
          <div>더치 페이</div>
          {/* 나가기 아이콘(-> 누르면 모달) */}
          <svg onClick={openModal} xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="32" height="32" viewBox="0 0 48 48" fill="#656565">
            <path d="M 11.5 6 C 8.4802259 6 6 8.4802259 6 11.5 L 6 36.5 C 6 39.519774 8.4802259 42 11.5 42 L 29.5 42 C 32.519774 42 35 39.519774 35 36.5 A 1.50015 1.50015 0 1 0 32 36.5 C 32 37.898226 30.898226 39 29.5 39 L 11.5 39 C 10.101774 39 9 37.898226 9 36.5 L 9 11.5 C 9 10.101774 10.101774 9 11.5 9 L 29.5 9 C 30.898226 9 32 10.101774 32 11.5 A 1.50015 1.50015 0 1 0 35 11.5 C 35 8.4802259 32.519774 6 29.5 6 L 11.5 6 z M 33.484375 15.484375 A 1.50015 1.50015 0 0 0 32.439453 18.060547 L 36.878906 22.5 L 15.5 22.5 A 1.50015 1.50015 0 1 0 15.5 25.5 L 36.878906 25.5 L 32.439453 29.939453 A 1.50015 1.50015 0 1 0 34.560547 32.060547 L 41.560547 25.060547 A 1.50015 1.50015 0 0 0 41.560547 22.939453 L 34.560547 15.939453 A 1.50015 1.50015 0 0 0 33.484375 15.484375 z"></path>
          </svg>
        </Title>
        <LinkBox>
          {
          // !memberSetComplete&&
            <input value={memberNum} type="number" placeholder="인원을 설정해주세요." onChange={(e) => {
              const value = e.target.value;
              setMemberNum(value === "" ? "" : Number(value));
            }}/>
          }

          {/* 사용자가 인원을 입력했을 경우에만 다음 화살표(->누르면 재확인 모달)가 나타나도록 함 */}
          {memberNum
          // &&!memberSetComplete
          ? 
            <svg onClick={onCheckComplete} xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="24" height="24" viewBox="0 0 48 48" fill="#ffffff">
              <path d="M 24 4 C 12.972066 4 4 12.972074 4 24 C 4 35.027926 12.972066 44 24 44 C 35.027934 44 44 35.027926 44 24 C 44 12.972074 35.027934 4 24 4 z M 24 7 C 33.406615 7 41 14.593391 41 24 C 41 33.406609 33.406615 41 24 41 C 14.593385 41 7 33.406609 7 24 C 7 14.593391 14.593385 7 24 7 z M 25.484375 16.484375 A 1.50015 1.50015 0 0 0 24.439453 19.060547 L 27.878906 22.5 L 16.5 22.5 A 1.50015 1.50015 0 1 0 16.5 25.5 L 27.878906 25.5 L 24.439453 28.939453 A 1.50015 1.50015 0 1 0 26.560547 31.060547 L 32.560547 25.060547 A 1.50015 1.50015 0 0 0 32.560547 22.939453 L 26.560547 16.939453 A 1.50015 1.50015 0 0 0 25.484375 16.484375 z"></path>
            </svg>
          :
            null
          }

          {/* {maxMember&&memberSetComplete&&<ShareUrl>{dutchUrl}</ShareUrl>} */}
        </LinkBox>
      </Top>

      <Main>
        {/* 3. 더치페이하는 상품 정보 */}
        {/* 2. 참여자 목록 컴포넌트_2단계인지 판단 기준: memberSetComplete === true */}
        {/* {memberSetComplete&&<Participant maxNum={Number(maxMember)} isHost={isHost} />} */}
      </Main>

      {/* 배경 도형 */}
      <BackImg>
        <img src={triangle}/>
        <img src={triangle}/>
        <img src={triangle}/>
      </BackImg>


{/* 모달 */}
      {/* 더치페이 인원 설정 확인용 모달 */}
      {isCompleteSettingCheck&&(
        <Modal isOpen={isCompleteSettingCheck} onClose={closeSettingModal}>
          <div>{memberNum}명과 더치페이를 진행하시겠습니까?</div>
          <div>
            <button onClick={completeMemberSetting}>확인</button>
            <button onClick={closeSettingModal}>취소</button>
          </div>
        </Modal>
      )}

      {/* [종료 버튼 미완]더치페이 나가기 모달 */}
      {isOpen && (
        <Modal isOpen={isOpen} onClose={closeModal}>
          <svg onClick={closeModal} xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="20" height="20" viewBox="0 0 48 48">
            <path d="M 38.982422 6.9707031 A 2.0002 2.0002 0 0 0 37.585938 7.5859375 L 24 21.171875 L 10.414062 7.5859375 A 2.0002 2.0002 0 0 0 8.9785156 6.9804688 A 2.0002 2.0002 0 0 0 7.5859375 10.414062 L 21.171875 24 L 7.5859375 37.585938 A 2.0002 2.0002 0 1 0 10.414062 40.414062 L 24 26.828125 L 37.585938 40.414062 A 2.0002 2.0002 0 1 0 40.414062 37.585938 L 26.828125 24 L 40.414062 10.414062 A 2.0002 2.0002 0 0 0 38.982422 6.9707031 z"></path>
          </svg>
          {stop ? 
            <div>정말 더치페이를 중단시키겠습니까?</div>
          :
            <div>더치페이를 중단 시키시겠습니까?</div>
          }
          <div>
            {/* <button onClick={closeModal}>취소</button> */}
            {/* 종료(중단)버튼: 더치페이 주최자는 더치페이가 모두에게 종료되도록하고 참가자는 참가자 본인만 종료되도록 해야함  */}
            {stop ? 
              <button>예</button>
            :
              <button onClick={onClickStop}>중단</button>
            }
            {stop ? 
              <button onClick={() => {setStop(false)}}>취소</button>
            :
              <button onClick={goHome}>홈으로</button>
            }
          </div>
        </Modal>
      )}

    </Wrapper>
  )
}

export default DutchOpen;