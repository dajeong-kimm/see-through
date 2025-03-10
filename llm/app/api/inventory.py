from fastapi import APIRouter
from app.schemas.inventory import InventoryUpdateRequest
from app.services.inventory_service import upsert_inventory

router = APIRouter()

@router.post("/upcreate_at_inventory")
async def update_inventory(request: InventoryUpdateRequest):
    """냉장고 재료를 Pinecone에 저장 또는 업데이트"""
    
    upsert_inventory(request)

    return {"message": "공유 냉장고 재료 업데이트 완료"}
