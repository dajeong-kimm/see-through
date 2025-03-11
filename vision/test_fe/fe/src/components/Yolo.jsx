import React, { useRef, useEffect, useState } from "react";
import axios from "axios";

const FaceDetection = () => {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const [faces, setFaces] = useState([]);

  useEffect(() => {
    // 웹캠 접근
    const startVideo = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true });
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }
      } catch (error) {
        console.error("웹캠 접근 오류:", error);
      }
    };

    startVideo();
  }, []);

  // 주기적으로 얼굴 탐지 요청 보내기
  useEffect(() => {
    const interval = setInterval(async () => {
      if (!videoRef.current) return;

      // 캔버스에서 현재 프레임 캡처
      const canvas = document.createElement("canvas");
      const video = videoRef.current;
      const context = canvas.getContext("2d");

      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      context.drawImage(video, 0, 0, canvas.width, canvas.height);

      canvas.toBlob(async (blob) => {
        if (!blob) return;
        const formData = new FormData();
        formData.append("file", blob, "frame.jpg");

        try {
          const response = await axios.post("http://localhost:8000/detect_faces/", formData);
          setFaces(response.data.faces);
        } catch (error) {
          console.error("얼굴 탐지 요청 오류:", error);
        }
      }, "image/jpeg");
    }, 200);

    return () => clearInterval(interval);
  }, []);

  // 얼굴 좌표를 캔버스에 그리기
  useEffect(() => {
    const canvas = canvasRef.current;
    const video = videoRef.current;
    if (!canvas || !video) return;

    const ctx = canvas.getContext("2d");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.strokeStyle = "red";
    ctx.lineWidth = 2;

    faces.forEach(({ x1, y1, x2, y2 }) => {
      ctx.strokeRect(x1, y1, x2 - x1, y2 - y1);
    });
  }, [faces]);

  return (
    <div style={{ position: "relative", width: "640px", height: "480px" }}>
      <video ref={videoRef} autoPlay playsInline style={{ width: "100%", height: "100%" }} />
      <canvas ref={canvasRef} style={{ position: "absolute", top: 0, left: 0 }} />
    </div>
  );
};

export default FaceDetection;
