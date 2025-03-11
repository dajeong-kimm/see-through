import openai
import os
from datetime import datetime
import json

client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

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

    comment = response.choices[0].message.content

    try:
        comment = json.loads(comment)  # JSON 내 escape된 문자 변환
    except json.JSONDecodeError:
        comment = comment.strip('"')  # JSON 변환이 불가능하면 수동으로 따옴표 제거

    return comment
