import React, { useEffect, useRef } from "react";
import styled from "styled-components";

// Styled-components for canvas container
const CanvasContainer = styled.div`
  position: absolute;
  z-index: 0;
  top: -100px;
  width: 100vw;
  height: 100vh;

  canvas {
    position: absolute;
    width: 100%;
    height: 100%;
  }
`;

const ParticleCanvas: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return; // canvas가 null인지 확인
    const ctx = canvas.getContext("2d");
    if (!ctx) return; // ctx가 null인지 확인

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    let particlesArray: Particle[] = [];
    const numberOfParticles = 150;
    let animationId: number;

    // Particle class (자연스러운 직사각형 모양)
    class Particle {
      x: number;
      y: number;
      width: number;
      height: number;
      speedX: number;
      speedY: number;
      color: string;
      angle: number;
      canvas: HTMLCanvasElement;
      ctx: CanvasRenderingContext2D;

      constructor(canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D) {
        this.canvas = canvas;
        this.ctx = ctx;
        this.x = Math.random() * canvas.width; // x좌표는 무작위
        this.y = Math.random() * canvas.height; // y좌표는 무작위로 시작
        this.width = Math.random() * 15 + 2; // 너비
        this.height = Math.random() * 10 + 2; // 높이
        this.speedX = Math.random() * 2 - 1; // 좌우로 약간의 이동
        this.speedY = Math.random() * 2 + 1; // 아래로 떨어지는 속도
        this.color = Math.random() > 0.5 ? "#ffffff94" : "#c61aed7e"; // 색상은 하얀색 또는 보라색
        this.angle = Math.random() * 360; // 회전 각도
      }

      update() {
        this.x += this.speedX; // 좌우 이동
        this.y += this.speedY; // 아래로 떨어짐

        // 파티클이 화면 하단에 도달하면 상단으로 리셋
        if (this.y > this.canvas.height) {
          this.y = -this.height; // 화면 상단 위로 리셋
          this.x = Math.random() * this.canvas.width; // 새로운 x 위치
        }

        this.angle += 2; // 회전 속도
      }

      draw() {
        this.ctx.save(); // 현재 상태 저장
        this.ctx.translate(this.x + this.width / 2, this.y + this.height / 2); // 파티클 중심 이동
        this.ctx.rotate((this.angle * Math.PI) / 180); // 각도 회전
        this.ctx.fillStyle = this.color;
        this.ctx.fillRect(
          -this.width / 2,
          -this.height / 2,
          this.width,
          this.height
        ); // 네모(직사각형) 그리기
        this.ctx.restore(); // 이전 상태 복원
      }
    }

    // Initialize particles
    function init() {
      particlesArray = [];
      for (let i = 0; i < numberOfParticles; i++) {
        particlesArray.push(new Particle(canvas!, ctx!)); // canvas와 ctx에 확정 할당 연산자(!) 사용
      }
    }

    // Animation loop
    function animate() {
      if (!ctx || !canvas) return; // null인지 다시 한 번 확인
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      particlesArray.forEach((particle) => {
        particle.update();
        particle.draw();
      });

      animationId = requestAnimationFrame(animate);
    }

    init();
    animate();

    // Resize canvas
    window.addEventListener("resize", () => {
      if (canvas) {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        init();
      }
    });

    return () => {
      window.removeEventListener("resize", () => {
        if (canvas) {
          canvas.width = window.innerWidth;
          canvas.height = window.innerHeight;
        }
      });
      cancelAnimationFrame(animationId); // 클린업 시 애니메이션 중지
    };
  }, []);

  return (
    <CanvasContainer>
      <canvas ref={canvasRef} />
    </CanvasContainer>
  );
};

export default ParticleCanvas;
