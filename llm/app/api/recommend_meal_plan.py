from fastapi import APIRouter, HTTPException
from app.services.recommend_meal_plan_service import recommend_meal_plan
from app.schemas.recommend_meal_plan import RecommendMealPlanRequest, RecommendMealPlanResponse

router = APIRouter()

@router.post("/recommend-meal-plan", response_model=RecommendMealPlanResponse)
async def recommend_meal_plan_endpoint(request: RecommendMealPlanRequest):
    """ LLM을 사용하여 멤버들의 식단을 추천하는 API """

    print(f"[DEBUG] API 요청 수신: {request.dict()}")  # 요청 데이터 출력

    try:
        meal_plan = recommend_meal_plan(request.member_ids, request.schedule, request.additional_request)

        # OpenAI 응답에서 `daily_meal_plans` 키를 꺼내 리스트로 변환
        if isinstance(meal_plan, dict) and "daily_meal_plans" in meal_plan:
            meal_plan_list = meal_plan["daily_meal_plans"]
        else:
            raise ValueError("OpenAI API 응답 형식이 올바르지 않습니다.")

        print(f"[DEBUG] 변환된 식단 계획: {meal_plan_list}")  # 수정된 데이터 출력
        return RecommendMealPlanResponse(day_count=request.day_count, daily_meal_plans=meal_plan_list)

    except Exception as e:
        print(f"[ERROR] API 처리 중 오류 발생: {e}")  # 에러 출력
        raise HTTPException(status_code=500, detail=str(e))
