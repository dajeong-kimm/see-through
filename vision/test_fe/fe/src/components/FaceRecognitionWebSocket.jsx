import { useRef, useState, useEffect } from "react";
import Webcam from "react-webcam";

export default function FaceRecognition() {
  const webcamRef = useRef(null);
  const canvasRef = useRef(null);
  const [ws, setWs] = useState(null);
  const [recognizedUser, setRecognizedUser] = useState(null);
  const [faceBox, setFaceBox] = useState(null);
  const [statusMessage, setStatusMessage] = useState("얼굴을 감지 중...");

  // ✅ WebSocket 연결 설정
  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8000/ws");

    socket.onopen = () => {
      console.log("✅ WebSocket 연결됨");
      setWs(socket);
    };

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      console.log("📩 서버 응답:", data);

      if (data.error) {
        setStatusMessage("❌ 얼굴을 감지할 수 없습니다.");
        setRecognizedUser(null);
        setFaceBox(null);
      } else if (data.result && data.result.length > 0) {
        const faceData = data.result[0];
        setRecognizedUser(faceData.identity);
        setFaceBox({
          x: faceData.source_x,
          y: faceData.source_y,
          width: faceData.source_w,
          height: faceData.source_h,
        });
        setStatusMessage(`✅ 환영합니다, ${faceData.identity} 님!`);
      } else {
        setRecognizedUser(null);
        setFaceBox(null);
        setStatusMessage("🔄 등록되지 않은 사용자입니다.");
      }

      // ✅ 응답을 받은 후 다시 `sendFrame()` 실행
      sendFrame();
    };

    socket.onerror = (error) => console.error("🚨 WebSocket 오류:", error);
    socket.onclose = () => console.log("❌ WebSocket 연결 종료");

    return () => {
      socket.close();
    };
  }, []);

  // ✅ WebSocket 또는 웹캠이 변경될 때마다 `sendFrame()` 실행
  useEffect(() => {
    if (ws && ws.readyState === 1 && webcamRef.current) {
      console.log("🚀 WebSocket & 웹캠 준비 완료 → `sendFrame()` 실행");
      sendFrame();
    }
  }, [ws, webcamRef]);

  // ✅ 응답을 받을 때마다 WebSocket으로 프레임을 전송하는 함수
  const sendFrame = () => {
    if (!ws || ws.readyState !== 1 || !webcamRef.current) return 1;

    const video = webcamRef.current.video;

    // ✅ 웹캠 비디오가 준비되지 않은 경우 재시도
    if (!video || video.readyState < 4) {
      console.log("⏳ 웹캠이 아직 준비되지 않음... 100ms 후 다시 시도");
      setTimeout(sendFrame, 100);
      return;
    }

    const imageSrc = webcamRef.current.getScreenshot();
    if (!imageSrc) {
      console.log("⏳ `getScreenshot()`이 null 반환... 100ms 후 다시 시도");
      setTimeout(sendFrame, 100);
      return;
    }

    console.log("📤 이미지 전송 중...");

    fetch(imageSrc)
      .then((res) => res.blob())
      .then((blob) => {
        const reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = () => {
          const base64Data = reader.result.split(",")[1];
          ws.send(base64Data);
        };
      });
  };

  // ✅ 얼굴 박스를 캔버스에 지속적으로 그리기
  useEffect(() => {
    if (!canvasRef.current || !webcamRef.current) return;

    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d");
    const video = webcamRef.current.video;

    const drawFaceBox = () => {
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (faceBox) {
        ctx.strokeStyle = "red";
        ctx.lineWidth = 3;
        ctx.strokeRect(faceBox.x, faceBox.y, faceBox.width, faceBox.height);
      }

      requestAnimationFrame(drawFaceBox);
    };

    drawFaceBox();
  }, [faceBox]);

  return (
    <div className="container mx-auto p-4 max-w-6xl">
      <h1 className="text-2xl font-bold mb-6">실시간 얼굴 인식 시스템</h1>

      <div className="relative w-full max-w-lg mx-auto">
        {/* 웹캠 */}
        <Webcam ref={webcamRef} screenshotFormat="image/jpeg" mirrored={true} className="w-full border rounded-lg" />

        {/* 얼굴 감지를 표시할 캔버스 */}
        <canvas ref={canvasRef} className="absolute top-0 left-0 w-full h-full pointer-events-none" />
      </div>

      {/* ✅ 상태 메시지 UI */}
      <div
        className={`mt-4 p-4 rounded-lg border ${
          statusMessage.includes("❌")
            ? "bg-red-50 border-red-200 text-red-700"
            : statusMessage.includes("✅")
            ? "bg-green-50 border-green-200 text-green-700"
            : "bg-yellow-50 border-yellow-200 text-yellow-700"
        }`}
      >
        <h4 className="font-medium">{statusMessage}</h4>
      </div>

      {/* ✅ 프레임 전송 버튼 */}
      <button
        onClick={sendFrame}
        className="mt-4 px-6 py-2 bg-blue-500 text-white font-semibold rounded-lg shadow-md hover:bg-blue-700"
      >
        📸 얼굴 인식 시작
      </button>
    </div>
  );
}
