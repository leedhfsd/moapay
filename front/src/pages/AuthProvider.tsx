import { useEffect   } from "react";
import { useNavigate } from "react-router-dom";

export default function  AuthProvider({children,}:{children:React.ReactNode}) {
    const navigate = useNavigate();
    useEffect(() => {
        //로그인된 유저는 접근할 수 없도록 처리
        const user =null;
        if(user){
            navigate("/");
        }
    },[]);
    return children;
}
