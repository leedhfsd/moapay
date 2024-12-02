import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
export default function ProtectedRoute({
  children,
}: {
  children: React.ReactNode;
}) {
  const navigate = useNavigate();

  /**
   * 로직
   * 로그인 여부를 따져서
   */
  useEffect(() => {}, [navigate]);

  return children;
}
