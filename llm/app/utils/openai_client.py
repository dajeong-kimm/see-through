import openai
import os
from datetime import datetime
import json
from app.db.pinecone_client import index

client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def get_openai_client():
    """ OpenAI API 클라이언트 인스턴스 생성 """
    return openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def get_openai_completion(prompt: str) -> str:
    """
    OpenAI GPT 모델을 사용하여 텍스트 생성
    """
    response = client.chat.completions.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=10  
    )
    
    raw_response = response.choices[0].message.content
    return clean_openai_response(raw_response) 

def clean_openai_response(response_text: str) -> str:
    """
    OpenAI의 응답 문자열에서 불필요한 따옴표 및 이스케이프 문자를 제거하는 함수
    """
    try:
        # JSON 형식이면 변환 후 반환
        cleaned_text = json.loads(response_text)
    except json.JSONDecodeError:
        # JSON 변환이 불가능하면 수동으로 양쪽 따옴표 제거
        cleaned_text = response_text.strip('"')

    return cleaned_text


def get_menu_recommendation_prompt(user_metadata: dict, additional_request: str) -> str:
    """사용자의 선호도를 고려하여 적절한 메뉴를 추천하는 프롬프트 생성"""

    age = user_metadata.get("age", "알 수 없음")
    preferred_foods = user_metadata.get("preferred_foods", [])
    disliked_foods = user_metadata.get("disliked_foods", [])
    
    prompt = f"""
    사용자의 나이는 {age}세이며, 선호하는 음식은 {', '.join(preferred_foods)}이고, 기피하는 음식은 {', '.join(disliked_foods)}입니다.
    {additional_request if additional_request else ""}
    
    위 정보를 바탕으로 오늘 먹을 만한 적절한 메뉴를 한 가지 추천해주세요.
    - 사용자가 좋아할 가능성이 높은 음식을 선택하세요.
    - 기피하는 음식은 절대 포함하지 마세요.
    - 메뉴 이름만 한 단어 또는 짧은 문장으로 출력하세요.
    """

    return prompt


def generate_personalized_comment(member_id: str, removed_ingredient: str, past_logs: list, user_metadata: dict, date: datetime):
    """ OpenAI API를 사용하여 사용자 맞춤형 코멘트 생성 """

    age = user_metadata.get("age", "알 수 없음")
    preferred_foods = user_metadata.get("preferred_foods", [])
    disliked_foods = user_metadata.get("disliked_foods", [])
    current_hour = date.hour
    time_of_day = "아침" if current_hour < 12 else "점심" if current_hour < 18 else "저녁"

    prompt = f"""
    사용자의 나이는 {age}세이며, 선호하는 음식은 {', '.join(preferred_foods)}이고, 기피하는 음식은 {', '.join(disliked_foods)}입니다.
    현재 사용자가 먹으려는 음식은 '{removed_ingredient}'이며, {time_of_day} 시간대입니다.
    최근 음식 섭취 기록: {past_logs}

    위 정보를 바탕으로 사용자가 현재 먹으려는 음식에 대한 짧고 재미있으면서도 유익한 코멘트를 한 줄만 작성해주세요.
    - 사용자가 좋아할 만한 음식이라면 긍정적인 반응을 주세요. (예: "스테이크를 좋아하는 당신에게 잘 어울리는 선택이군요!")
    - 건강에 부정적인 요소가 있다면 경고해주세요. (예: "늦은 밤 카페인 섭취는 숙면을 방해할 수 있어요.")
    - 음식과 시간대를 고려하여 적절한 피드백을 주세요.
    - **절대 인사말 없이 한 줄로만 작성하세요.**
    """


    response = client.chat.completions.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=100
    )

    raw_comment = response.choices[0].message.content
    return clean_openai_response(raw_comment)

def get_meal_plan_prompt(members_metadata: dict, schedule: list, additional_request: str) -> str:
    """냉장고 속 재료를 고려한 LLM 식단 추천 프롬프트 생성"""

    # 🔹 냉장고 속 재료 가져오기
    fridge_ingredients = get_inventory_ingredients()

    prompt = f"""
    # 사용자 맞춤 식단 계획 생성 (JSON 형식만 반환)

    아래 사용자들의 식단 계획을 JSON 형식으로 반환하세요:
    {json.dumps(members_metadata, indent=2, ensure_ascii=False)}

    ## 냉장고 속 재료:
    {json.dumps(fridge_ingredients, indent=2, ensure_ascii=False)}

    ## 식단 일정:
    {json.dumps(schedule, indent=2, ensure_ascii=False)}

    ## 추가 요청 사항:
    {json.dumps(additional_request, indent=2, ensure_ascii=False)}

    ## 반드시 지킬 사항:
    - **반드시 JSON 형식으로만 응답하세요.**
    - **"```json" 또는 코드 블록을 포함하지 마세요.**
    - **설명, 주석, 코드 블록을 절대 포함하지 마세요.**
    - **JSON 이외의 불필요한 문장은 작성하지 마세요.**
    - 냉장고 속 재료를 최대한 활용하세요.
    - 모든 사용자의 선호 음식과 기피 음식을 고려하세요.
    - 일정에 맞춰 아침(BREAKFAST), 점심(LUNCH), 저녁(DINNER) 메뉴를 제공합니다.
    - 반드시 고단백 저염식을 반영하여 작성하세요.
    - **각 식사 메뉴는 5개의 음식으로 구성하세요.**

    ## JSON 응답 예시 (설명 없이 JSON만 출력):
    {json.dumps({
        "day_count": 2,
        "daily_meal_plans": [
            {
                "date": "2025-12-31",
                "meals": [
                    {"serving_time": "BREAKFAST", "menu": ["양파 오믈렛", "삶은 달걀", "바나나", "요거트", "오트밀"]},
                    {"serving_time": "LUNCH", "menu": ["닭가슴살 샐러드", "아보카도", "토마토", "올리브오일", "현미밥"]},
                    {"serving_time": "DINNER", "menu": ["연어구이", "구운 감자", "퀴노아", "두부", "브로콜리"]}
                ]
            },
            {
                "date": "2026-01-01",
                "meals": [
                    {"serving_time": "LUNCH", "menu": ["된장찌개", "현미밥", "김치", "찐 감자", "양배추 샐러드"]},
                    {"serving_time": "DINNER", "menu": ["고등어구이", "된장찌개", "나물 무침", "대파 볶음", "두부 부침"]}
                ]
            }
        ]
    }, indent=2, ensure_ascii=False)}

    **위 JSON 형식 외의 텍스트를 절대 포함하지 마세요.**
    """

    return prompt

def get_inventory_ingredients() -> list:
    """Pinecone에서 냉장고에 있는 모든 재료 조회"""
    namespace = "fridge"

    # Pinecone에서 저장된 벡터 개수 가져오기
    index_stats = index.describe_index_stats()

    # 네임스페이스 존재 여부 확인
    if namespace not in index_stats["namespaces"]:
        return []  # 네임스페이스가 없으면 빈 리스트 반환

    vector_count = index_stats["namespaces"][namespace].get("vector_count", 0)
    if vector_count == 0:
        return []  # 저장된 벡터가 없으면 빈 리스트 반환

    # 저장된 벡터 ID 목록 조회
    ingredient_ids = [str(i) for i in range(vector_count)]  # 저장된 벡터 ID 리스트 생성

    # Pinecone에서 기존 재료 조회
    fetch_response = index.fetch(ids=ingredient_ids, namespace=namespace)

    if not fetch_response.vectors:
        return []

    # 존재하는 벡터 ID 추출
    existing_ingredient_ids = fetch_response.vectors.keys()

    # 벡터의 메타데이터에서 재료명 추출
    ingredients = [fetch_response.vectors[vec_id].metadata["ingredient"] 
                   for vec_id in existing_ingredient_ids if "metadata" in fetch_response.vectors[vec_id]]

    return ingredients


