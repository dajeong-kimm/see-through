import React, { useState } from "react";
import axios from "axios";

const FaceDetection = () => {
  const [faceData, setFaceData] = useState(null);
  const [faceImage, setFaceImage] = useState(null);
  const [identifiedUsers, setIdentifiedUsers] = useState([]);
  const [userId, setUserId] = useState(null);

  const detectFace = async () => {
    try {
      const response = await axios.get("http://localhost:8000/detect_faces/");
      if (response.data.face) {
        setFaceData(response.data.face);
        setFaceImage(`data:image/jpeg;base64,${response.data.face_image}`);
        setIdentifiedUsers(response.data.identified_users || []);
      } else {
        setFaceData(null);
        setFaceImage(null);
        setIdentifiedUsers([]);
        alert("얼굴을 찾을 수 없습니다.");
      }
    } catch (error) {
      console.error("Error detecting face:", error);
      alert("얼굴 감지 요청에 실패했습니다.");
    }
  };

  const registerUser = async () => {
    if (!faceImage) return alert("등록할 얼굴 이미지가 없습니다.");
    try {
      const blob = await fetch(faceImage).then((res) => res.blob());
      const formData = new FormData();
      formData.append("file", blob, "detected_face.jpg");

      const response = await axios.post("http://localhost:8000/register_user/", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      if (response.data.status === "success") {
        setUserId(response.data.user_id);
        alert("사용자 등록이 완료되었습니다!");
      } else {
        alert("사용자 등록에 실패했습니다: " + response.data.message);
      }
    } catch (error) {
      console.error("Error registering user:", error);
      alert("사용자 등록 요청에 실패했습니다.");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">YOLO Face Detection</h1>
      <button
        onClick={detectFace}
        className="px-6 py-3 bg-blue-500 text-white rounded-lg shadow-md hover:bg-blue-600 transition"
      >
        Detect Face
      </button>
      {faceData && (
        <div className="mt-6 p-6 bg-white rounded-lg shadow-lg w-full max-w-md text-center">
          <h2 className="text-xl font-semibold text-gray-700">Detected Face</h2>
          <p className="text-gray-600 mt-2">
            좌표: X1: {faceData.x1}, Y1: {faceData.y1}, X2: {faceData.x2}, Y2: {faceData.y2}
          </p>
          {faceImage && (
            <img
              src={faceImage}
              alt="Detected Face"
              className="mt-4 w-48 h-48 object-cover border-4 border-green-500 rounded-lg"
            />
          )}
          {identifiedUsers.length > 0 ? (
            <div className="mt-4">
              <h3 className="text-lg font-medium text-gray-700">Identified Users</h3>
              <ul className="mt-2 text-gray-600">
                {identifiedUsers.map((user, index) => (
                  <li key={index} className="border-b py-2">
                    {user.identity} (Distance: {user.distance})
                  </li>
                ))}
              </ul>
            </div>
          ) : (
            <div className="mt-4">
              <p className="text-gray-500">사용자를 식별하지 못했습니다.</p>
              <button
                onClick={registerUser}
                className="mt-3 px-6 py-2 bg-green-500 text-white rounded-lg shadow-md hover:bg-green-600 transition"
              >
                Register as New User
              </button>
            </div>
          )}
        </div>
      )}
      {userId && <p className="mt-4 text-green-600 font-semibold">등록된 사용자 ID: {userId}</p>}
    </div>
  );
};

export default FaceDetection;
