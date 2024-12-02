import {
  ModalOverlay,
  ModalContent,
} from "./Modal.styles"
import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
}

const Modal = ({ isOpen, onClose, children }:ModalProps) => {
  if (!isOpen) return null; // 모달이 열리지 않으면 렌더링하지 않음

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={(e) => e.stopPropagation()}>
        {children}
      </ModalContent>
    </ModalOverlay>
  );
};

export default Modal;