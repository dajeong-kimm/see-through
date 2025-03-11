from app.db.pinecone_client import index
from app.utils.openai_client import generate_personalized_comment
from app.utils.embedding import get_embedding
from app.schemas.personal_notice import PersonalNoticeRequest
import logging
import base64

def encode_to_ascii(text: str) -> str:
    """한글 포함된 문자열을 ASCII 문자열로 변환"""
    return base64.urlsafe_b64encode(text.encode()).decode()

def generate_personal_notice(request: PersonalNoticeRequest) -> str:
    """ 사용자의 정보 및 소비 패턴을 기반으로 맞춤형 피드백 생성 """

    member_id = request.member_id
    removed_ingredient = request.removed_ingredient

    # 1. 사용자 정보 조회 (namespace=member_id)
    # ID 변환 적용
    profile_id = encode_to_ascii("profile")

    logging.info(f"사용자 정보 조회 시도: namespace={member_id}, id={profile_id}")
    user_info_response = index.fetch(ids=[profile_id], namespace=member_id)
    logging.info(f"조회된 사용자 정보: {user_info_response.vectors}")

    user_vector = user_info_response.vectors.get(profile_id)  

    if not user_vector:
        logging.error(f"사용자 정보를 찾을 수 없습니다. namespace={member_id}, id={profile_id}")
        raise ValueError("사용자 정보를 찾을 수 없습니다.")

    user_metadata = user_vector.metadata
    logging.info(f"사용자 정보: {user_metadata}")

    # 2. 최근 음식 섭취 기록 조회 (namespace="food_log")
    food_log_response = index.query(
        namespace="food_log",
        top_k=10,  
        include_metadata=True,
        filter={"user_id": member_id}
    )

    past_logs = [entry.metadata for entry in food_log_response.matches]
    logging.info(f"{member_id}의 최근 음식 기록: {past_logs}")

    # 3. OpenAI GPT를 사용하여 개인화된 피드백 생성
    comment = generate_personalized_comment(
        member_id, removed_ingredient, past_logs, user_metadata, request.date
    )

    return comment

def save_food_log_to_pinecone(request: PersonalNoticeRequest):
    """ 사용자의 음식 섭취 정보를 Pinecone에 저장 """

    member_id = request.member_id
    removed_ingredient = request.removed_ingredient

    log_text = f"{member_id}가 {request.date}에 {removed_ingredient}을(를) 먹음"
    metadata = {
        "user_id": member_id,
        "food": removed_ingredient,
        "date": request.date.isoformat()
    }

    data_id = encode_to_ascii(f"{member_id}_{request.date}")  # 고유 ID 생성
    vector = get_embedding(log_text)  # 텍스트 임베딩

    # Pinecone에 저장
    index.upsert([(data_id, vector, metadata)], namespace=member_id)

    logging.info(f"{member_id}의 음식 섭취 기록 저장 완료: {metadata}")