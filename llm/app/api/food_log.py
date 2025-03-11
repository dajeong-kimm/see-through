# 음식 소비(섭취) 기록 API

from fastapi import APIRouter
from app.services.food_log_service import save_to_pinecone_bulk
from app.schemas.food_log import FoodLogRequest

router = APIRouter()

@router.post("/log-food")
async def log_food(request: FoodLogRequest):
    """여러 개의 음식 섭취 기록을 Pinecone에 저장"""
    batch_data = []

    for entry in request.logs:
        text = f"{entry.member_id}가 {entry.date}에 {entry.food}을(를) 먹음"
        metadata = {
            "user_id": entry.member_id,
            "food": entry.food,
            "date": entry.date.isoformat()  # datetime을 문자열로 변환
        }

        batch_data.append({
            "data_id": f"{entry.member_id}_{entry.date}",
            "text": text,
            "metadata": metadata,
            "namespace": entry.member_id
        })

    # Pinecone에 배치 저장
    save_to_pinecone_bulk(batch_data)

    return {"message": f"{len(request.logs)}개의 음식 섭취 기록 완료"}
