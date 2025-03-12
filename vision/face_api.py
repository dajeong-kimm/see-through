from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from deepface import DeepFace
import uvicorn
import numpy as np
import tempfile
import os
import uuid
import cv2
import torch
from ultralytics import YOLO

app = FastAPI()

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

#------------------YOLO----------------------
yolo_model = YOLO("yolov11n-face.pt")

# YOLO를 활용한 얼굴 감지 API
@app.post("/detect_faces/")
async def detect_faces(file: UploadFile = File(...)):
    """
    YOLO를 이용하여 얼굴을 감지하고 좌표를 반환하는 API
    """

    # 업로드된 파일을 임시 저장
    with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
        temp_file.write(await file.read())
        temp_file_path = temp_file.name

    try:
        # 이미지 로드
        img = cv2.imread(temp_file_path)
        if img is None:
            raise ValueError("이미지를 읽을 수 없습니다.")

        # YOLO를 이용한 얼굴 탐지
        results = yolo_model(img)  # YOLO 실행
        faces = []

        for result in results:
            boxes = result.boxes.xyxy  # YOLO가 반환한 bounding box 좌표
            for box in boxes:
                x1, y1, x2, y2 = map(int, box.tolist())  # 좌표 정수 변환
                faces.append({"x1": x1, "y1": y1, "x2": x2, "y2": y2})

        os.remove(temp_file_path)  # 사용 후 파일 삭제
        return {"faces": faces}

    except Exception as e:
        return {"status": "error", "message": str(e)}
    
# YOLO로 뽑은 얼굴 이미지로 기존 사용자 찾아보기
@app.post("/recognize_faces/")
async def recognize_faces(file: UploadFile = File(...)):
    """
    YOLO로 뽑은 얼굴 이미지로 기존 사용자 인식
    """

    # 업로드된 파일을 임시 저장
    with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
        temp_file.write(await file.read())
        temp_file_path = temp_file.name

    try:
        # 이미지 로드
        img = cv2.imread(temp_file_path)
        if img is None:
            raise ValueError("이미지를 읽을 수 없습니다.")
        
        # 기존 사용자 인식
        result = []
        
        # 얼굴 인식 수행
        dfs = DeepFace.find(
            img_path=temp_file_path,
            db_path=db_path,
            model_name=model,
            detector_backend="skip",
            silent=True,
            threshold=0.3
        )
        
        os.remove(temp_file_path)  # 사용 후 파일 삭제
        
        # Pandas DataFrame이 반환되면, JSON 변환 전에 Python 기본 타입으로 변환
        if isinstance(dfs, list) and len(dfs) > 0:
            df = dfs[0]  # DeepFace.find()는 리스트 안에 DataFrame을 반환함
            result = df.applymap(lambda x: int(x) if isinstance(x, (np.int64, np.int32)) else x).to_dict(orient="records")
        else:
            result = []

        return {"result": result}
        
    except Exception as e:
        return {"status": "error", "message": str(e)}

#------------------DeepFace----------------------

detector_backend = "retinaface"
model = "Facenet"
db_path = "users"

# 얼굴 식별 API
@app.post("/find_faces/")
async def find_faces(file: UploadFile = File(...)):
    """
    업로드된 이미지를 분석하여 데이터베이스에서 가장 유사한 얼굴을 찾음
    """

    # 업로드된 파일을 임시 저장
    with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
        temp_file.write(await file.read())
        temp_file_path = temp_file.name

    try:
        # 얼굴 인식 수행
        dfs = DeepFace.find(
            img_path=temp_file_path,
            db_path=db_path,
            model_name=model,
            detector_backend=detector_backend,
            silent=True,
            threshold=0.3
        )

        os.remove(temp_file_path)  # 사용 후 파일 삭제
        
        # Pandas DataFrame이 반환되면, JSON 변환 전에 Python 기본 타입으로 변환
        if isinstance(dfs, list) and len(dfs) > 0:
            df = dfs[0]  # DeepFace.find()는 리스트 안에 DataFrame을 반환함
            result = df.applymap(lambda x: int(x) if isinstance(x, (np.int64, np.int32)) else x).to_dict(orient="records")
        else:
            result = []

        return {"result": result}

    except Exception as e:
        return {"status": "error", "message": str(e)}
    
# 신규 사용자 등록 API
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
    
if __name__ == "__main__":
    uvicorn.run("face_api:app", host="0.0.0.0", port=8000, reload=True, log_level="debug")

