import { createRoot } from "react-dom/client";
import App from "./App.tsx";

createRoot(document.getElementById("root")!).render(<App />);

// 서비스 워커 등록
if ("serviceWorker" in navigator) {
  navigator.serviceWorker
    .register("/firebase-messaging-sw.js") // 서비스 워커 파일 경로
    .then((registration) => {
      // console.log("Service Worker Registered:", registration);
    })
    .catch((error) => {
      // console.log("Service Worker registration failed:", error);
    });
}
