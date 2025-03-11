from fastapi import APIRouter, HTTPException
from app.schemas.inventory import InventoryUpdateRequest, InventoryDeleteRequest
from app.services.inventory_service import upsert_inventory, delete_inventory

router = APIRouter()

@router.post("/update_ingredient")
async def update_inventory(request: InventoryUpdateRequest):
    """냉장고 재료를 Pinecone에 저장 또는 업데이트"""
    
    upsert_inventory(request)

    return {"message": "공유 냉장고 재료 업데이트 완료"}

@router.delete("/remove_ingredient")
async def remove_ingredient(request: InventoryDeleteRequest):
    """냉장고 재료를 Pinecone에서 삭제"""
    
    try:
        delete_inventory(request)
        return {"message": f"{', '.join(request.ingredients)} 재료 삭제 완료"}
    except HTTPException as e:
        raise e
    except Exception:
        raise HTTPException(status_code=500, detail="서버 내부 오류 발생")