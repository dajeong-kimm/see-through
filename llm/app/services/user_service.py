from app.db.pinecone_client import index
from app.schemas.user import UserProfile
from app.utils.embedding import get_embedding
import base64
import logging

def encode_to_ascii(text: str) -> str:
    """한글 포함된 문자열을 ASCII 문자열로 변환"""
    return base64.urlsafe_b64encode(text.encode()).decode()

def upsert_user_profile(user: UserProfile):
    """사용자 정보를 Pinecone에 저장 또는 업데이트 (임베딩 적용)"""
    
    # 사용자 정보를 저장할 네임스페이스 (member_id 사용)
    namespace = user.member_id

    # metadata 구성 (datetime 대신 문자열 저장)
    metadata = {
        "age": user.age,
        "preferred_foods": user.preferred_foods,
        "disliked_foods": user.disliked_foods
    }

    # 사용자 정보는 "profile"이라는 ID로 저장
    user_id = encode_to_ascii("profile")

    # 선호 음식 & 기피 음식 데이터를 하나의 문장으로 변환
    user_text = f"선호 음식: {', '.join(user.preferred_foods)} / 기피 음식: {', '.join(user.disliked_foods)}"

    # 임베딩 벡터 변환
    user_vector = get_embedding(user_text)  # 실제 벡터 임베딩 사용

    # Pinecone에 저장 (user_id를 key로 사용)
    index.upsert(
        vectors=[(user_id, user_vector, metadata)],  # 임베딩된 벡터 추가
        namespace=namespace
    )
    logging.info(f"Pinecone 저장 완료: namespace={namespace}, id=profile")
