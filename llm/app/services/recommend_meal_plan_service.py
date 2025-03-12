import openai
import base64
from typing import List, Dict
from app.db.pinecone_client import index
from app.utils.openai_client import get_openai_client, get_meal_plan_prompt, clean_openai_response

client = get_openai_client()

def get_user_metadata(member_id: str) -> Dict:
    """Pinecone에서 사용자 프로필 조회"""
    profile_id = base64.urlsafe_b64encode("profile".encode()).decode()
    user_info_response = index.fetch(ids=[profile_id], namespace=member_id)

    if not user_info_response.vectors:
        raise ValueError("사용자 정보를 찾을 수 없습니다.")

    return user_info_response.vectors[profile_id].metadata

def recommend_meal_plan(member_ids: List[str], schedule: List[Dict], additional_request: str = None) -> List[Dict]:
    """OpenAI를 사용하여 멤버별 맞춤형 식단 추천"""

    print(f"[DEBUG] 요청된 member_ids: {member_ids}")
    print(f"[DEBUG] 요청된 schedule: {schedule}")
    print(f"[DEBUG] 추가 요청 사항: {additional_request}")

    # 모든 멤버의 선호도 데이터 조회
    members_metadata = {}
    for member in member_ids:
        try:
            members_metadata[member] = get_user_metadata(member)
        except Exception as e:
            print(f"[ERROR] {member}의 사용자 정보 조회 중 오류 발생: {e}")
            raise

    print(f"[DEBUG] 가져온 사용자 선호 데이터: {members_metadata}")

    # OpenAI 프롬프트 생성
    try:
        prompt = get_meal_plan_prompt(members_metadata, schedule, additional_request)
    except Exception as e:
        print(f"[ERROR] 프롬프트 생성 중 오류 발생: {e}")
        raise

    print(f"[DEBUG] 생성된 프롬프트:\n{prompt}")

    # OpenAI API 호출
    try:
        response = client.chat.completions.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}],
            max_tokens=1000
        )
    except Exception as e:
        print(f"[ERROR] OpenAI API 호출 중 오류 발생: {e}")
        raise

    print(f"[DEBUG] OpenAI API 응답:\n{response}")

    try:
        meal_plan = clean_openai_response(response.choices[0].message.content)
    except Exception as e:
        print(f"[ERROR] OpenAI 응답 처리 중 오류 발생: {e}")
        raise

    print(f"[DEBUG] 최종 생성된 식단:\n{meal_plan}")

    return meal_plan


