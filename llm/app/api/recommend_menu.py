from fastapi import APIRouter, HTTPException
from app.services.recommend_menu_service import recommend_menu
from app.schemas.recommend_menu import RecommendMenuRequest, RecommendMenuResponse

router = APIRouter()

@router.post("/recommend-menu", response_model=RecommendMenuResponse)
async def recommend_menu_endpoint(request: RecommendMenuRequest):
    """ 사용자의 요청을 기반으로 추천 메뉴 제공 """
    try:
        recommended_menu = recommend_menu(request.member_id, request.additional_request)
        return RecommendMenuResponse(recommended_menu=recommended_menu)

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
