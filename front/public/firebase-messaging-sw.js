// firebase-messaging-sw.js
importScripts("https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js");
importScripts(
  "https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js"
);

// Firebase 설정
firebase.initializeApp({
  apiKey: "AIzaSyDzoUP1IDHrb8ACMS3YIEs00Jbl9lFGB9A",
  authDomain: "moapay-9e55b.firebaseapp.com",
  projectId: "moapay-9e55b",
  storageBucket: "moapay-9e55b.appspot.com",
  messagingSenderId: "871862370248",
  appId: "1:871862370248:web:3adb855c9201ca98a2cafd",
});

// 메시징 인스턴스 생성
const messaging = firebase.messaging();

self.addEventListener("push", function (e) {
  const data = e.data.json(); // JSON 데이터 파싱

  const notificationTitle = data.notification.title || "Default Title";
  const notificationOptions = {
    body: data.notification.body || "Default Body",
    icon: data.notification.icon || "/default-icon.png",
  };

  console.log(data.notification.title, {
    body: data.notification.body,
  });

  // self.registration.showNotification(notificationTitle, notificationOptions);
});

self.addEventListener("install", function (e) {
  self.skipWaiting();
});

self.addEventListener("activate", function (e) {
  console.log("fcm sw activate..");
});

// 백그라운드 메시지 수신 처리
// messaging.onBackgroundMessage((payload) => {
//   console.log("Received background message ", payload);
//   const notificationTitle = payload.notification.title;
//   const notificationOptions = {
//     body: payload.notification.body,
//     icon: payload.notification.icon,
//   };

//   self.registration.showNotification(notificationTitle, notificationOptions);
// });
