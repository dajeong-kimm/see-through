from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
import cv2
import torch
from ultralytics import YOLO
import numpy as np
import base64
from io import BytesIO
import tempfile
import os
import uuid
from deepface import DeepFace
import pandas as pd

app = FastAPI()

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# YOLO 모델 로드
yolo_model = YOLO("yolov11n-face.pt")

# DeepFace 설정
db_path = "users"  # 기존 사용자 데이터베이스 경로
model = "Facenet"

# 웹캠 초기화
cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
if not cap.isOpened():
    raise RuntimeError("웹캠을 열 수 없습니다.")

# 최신 프레임 저장 변수
global_frame = None

@app.get("/detect_faces/")
def detect_faces():
    """
    현재 저장된 최신 웹캠 프레임에서 YOLO 얼굴 감지 수행 후, 가장 큰 얼굴 하나만 반환
    """
    global global_frame

    if global_frame is None:
        return {"status": "error", "message": "웹캠 프레임을 가져올 수 없습니다."}

    frame = global_frame.copy()

    # YOLO 얼굴 감지 실행
    results = yolo_model(frame)
    faces = []

    for result in results:
        boxes = result.boxes.xyxy  # YOLO가 반환한 bounding box 좌표
        for box in boxes:
            x1, y1, x2, y2 = map(int, box.tolist())  # 좌표 정수 변환
            faces.append({"x1": x1, "y1": y1, "x2": x2, "y2": y2})

    # 가장 큰 얼굴 선택 (면적 기준)
    if faces:
        largest_face = max(faces, key=lambda f: (f["x2"] - f["x1"]) * (f["y2"] - f["y1"]))
        x1, y1, x2, y2 = largest_face["x1"], largest_face["y1"], largest_face["x2"], largest_face["y2"]
        
        # 얼굴 크롭
        face_crop = frame[y1:y2, x1:x2]
        _, buffer = cv2.imencode(".jpg", face_crop)
        face_base64 = base64.b64encode(buffer).decode("utf-8")
        
        # 임시 저장 후 DeepFace를 사용해 기존 사용자와 비교
        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
            temp_file.write(buffer)
            temp_file_path = temp_file.name
            
        try:
            # 기존 사용자 인식
            dfs = DeepFace.find(
                img_path=temp_file_path,
                db_path=db_path,
                model_name=model,
                detector_backend="skip",
                silent=True,
                threshold=0.3,
            )
            os.remove(temp_file_path)  # 사용 후 파일 삭제
            
            # Pandas DataFrame을 JSON 변환
            if isinstance(dfs, list) and len(dfs) > 0:
                df = dfs[0]  # DeepFace.find()는 리스트 안에 DataFrame을 반환
                identified_users = df.applymap(lambda x: int(x) if isinstance(x, (np.int64, np.int32)) else x).to_dict(orient="records")
            else:
                identified_users = []
        except Exception as e:
            identified_users = []


        return {"face": largest_face, "face_image": face_base64, "identified_users": identified_users}
    
    return {"status": "error", "message": "얼굴을 찾을 수 없습니다."}

@app.post("/register_user/")
async def register_user_endpoint(file: UploadFile = File(...)):
    """
    업로드된 이미지 파일을 받아 UUID로 생성된 폴더에 저장
    """
    with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
        temp_file.write(await file.read())
        temp_file_path = temp_file.name
    try:
        # UUID로 고유 사용자 ID 생성
        user_id = str(uuid.uuid4())
        
        DeepFace.update(
            user_id=user_id,
            image_path=temp_file_path,
            db_path=db_path,
            model_name=model,
            detector_backend="skip",
            silent=True
        )
        
        os.remove(temp_file_path)
        
        return {"status": "success", "user_id": user_id}
    except Exception as e:
        return {"status": "error", "message": str(e)}

def run_webcam():
    """
    웹캠을 계속 실행하면서 OpenCV 창을 유지하고, 최신 프레임을 global_frame에 저장
    """
    global global_frame
    while True:
        ret, frame = cap.read()
        if ret:
            global_frame = frame.copy()

            # YOLO 얼굴 감지 실행
            results = yolo_model(frame)
            for result in results:
                boxes = result.boxes.xyxy  # YOLO가 반환한 bounding box 좌표
                for box in boxes:
                    x1, y1, x2, y2 = map(int, box.tolist())  # 좌표 정수 변환
                    cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                    cv2.putText(frame, "Face", (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

            # OpenCV 창에 출력
            cv2.imshow("YOLO Face Detection", frame)

        # 'q' 키를 누르면 종료
        if cv2.waitKey(1) & 0xFF == ord("q"):
            cap.release()
            cv2.destroyAllWindows()
            break

if __name__ == "__main__":
    import threading
    # OpenCV 실행을 위한 별도 함수 호출 (싱글 스레드 유지)
    threading.Thread(target=run_webcam, daemon=True).start()

    # FastAPI 서버 실행
    uvicorn.run(app, host="0.0.0.0", port=9000, reload=False, log_level="debug")