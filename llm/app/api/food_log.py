# 음식 소비(섭취) 기록 API

from fastapi import APIRouter, HTTPException
from app.services.food_log_service import save_to_pinecone_bulk
from app.services.inventory_service import delete_inventory
from app.schemas.food_log import FoodLogRequest
from app.schemas.inventory import InventoryDeleteRequest

router = APIRouter()

@router.post("/log-food")
async def log_food(request: FoodLogRequest):
    """여러 개의 음식 섭취 기록을 Pinecone에 저장"""
    batch_data = []
    consumed_foods = set()  # 중복 제거를 위해 set 사용

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

        consumed_foods.add(entry.food)  # 중복된 음식 제거 후 목록에 추가

    # Pinecone에 배치 저장
    save_to_pinecone_bulk(batch_data)

    # 냉장고에서 해당 음식 제거
    try:
        delete_inventory(InventoryDeleteRequest(ingredients=list(consumed_foods)))
    except HTTPException as e:
        if e.status_code == 404:
            return {
                "message": f"{len(request.logs)}개의 음식 섭취 기록 완료",
                "warning": e.detail  # 일부 재료가 없을 경우 경고 메시지 반환
            }
        raise e  # 다른 오류는 그대로 전파

    return {"message": f"{len(request.logs)}개의 음식 섭취 기록 완료 및 {', '.join(consumed_foods)} 냉장고에서 제거됨"}
