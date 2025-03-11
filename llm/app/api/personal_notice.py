from fastapi import APIRouter, HTTPException
from app.services.personal_notice_service import generate_personal_notice, save_food_log_to_pinecone
from app.schemas.personal_notice import PersonalNoticeRequest, PersonalNoticeResponse

router = APIRouter()

@router.post("/personal-notice", response_model=PersonalNoticeResponse)
async def personal_notice(request: PersonalNoticeRequest):
    """ 사용자의 프로필과 섭취 패턴을 분석하여 개인 맞춤 코멘트 제공 및 Pinecone에 저장 """
    try:
        # 개인 맞춤 코멘트 생성
        comment = generate_personal_notice(request)

        # 섭취 기록을 Pinecone에 저장
        save_food_log_to_pinecone(request)

        return PersonalNoticeResponse(notice_message=comment)

    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
