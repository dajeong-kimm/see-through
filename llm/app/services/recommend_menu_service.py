import openai
import os
from app.utils.openai_client import get_openai_client
from app.utils.openai_client import get_menu_recommendation_prompt, clean_openai_response
from app.db.pinecone_client import index
import base64

client = get_openai_client()

def get_user_metadata(member_id: str):
    """Pinecone에서 사용자 프로필 조회"""
    profile_id = base64.urlsafe_b64encode("profile".encode()).decode()  # ID 변환
    user_info_response = index.fetch(ids=[profile_id], namespace=member_id)

    if not user_info_response.vectors:
        raise ValueError("사용자 정보를 찾을 수 없습니다.")

    return user_info_response.vectors[profile_id].metadata

def recommend_menu(member_id: str, additional_request: str = None) -> str:
    """사용자의 선호도를 기반으로 OpenAI를 활용해 메뉴 추천"""

    # 사용자 정보 조회
    user_metadata = get_user_metadata(member_id)

    # 프롬프트 생성
    prompt = get_menu_recommendation_prompt(user_metadata, additional_request)

    # OpenAI API 호출
    response = client.chat.completions.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=50
    )

    return clean_openai_response(response.choices[0].message.content)
