from fastapi import APIRouter
from app.schemas.user import UserProfile
from app.services.user_service import upsert_user_profile

router = APIRouter()

@router.put("/update_user")
async def update_user_profile(user: UserProfile):
    """사용자 정보를 Pinecone에 저장 또는 업데이트"""
    
    upsert_user_profile(user)

    return {"message": "사용자 정보 업데이트 완료"}
