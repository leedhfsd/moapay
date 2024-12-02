import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import { VitePWA } from "vite-plugin-pwa";
import path from "path";

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: "autoUpdate",
      manifest: {
        name: "MoAPay",
        short_name: "MoAPay",
        description: "My awesome Vite-powered PWA!",
        theme_color: "#ffffff",
        icons: [
          {
            src: "./icons/apple-touch-icon-57x57.png",
            sizes: "57x57",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-60x60.png",
            sizes: "60x60",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-72x72.png",
            sizes: "72x72",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-76x76.png",
            sizes: "76x76",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-114x114.png",
            sizes: "114x114",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-120x120.png",
            sizes: "120x120",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-144x144.png",
            sizes: "144x144",
            type: "image/png",
          },
          {
            src: "./icons/apple-touch-icon-152x152.png",
            sizes: "152x152",
            type: "image/png",
          },
        ],
      },
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "/src"), // src 디렉토리 경로 별칭
    },
  },
  base: "/", // 빌드 시 루트 경로에서 리소스를 참조하도록 설정
  optimizeDeps: {
    include: ['swiper'], // swiper 포함
  },
});
