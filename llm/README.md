# 🍽️ FastAPI 기반 냉장고 & 식단 관리 API

## 🚀 실행 방법
### Windows (PowerShell)
```
# 가상 환경 활성화
venv\Scripts\Activate

# FastAPI 서버 실행
python -m uvicorn app.main:app --reload

```

### Windows (CMD)
```
# 가상 환경 활성화
venv\Scripts\activate.bat

# FastAPI 서버 실행
python -m uvicorn app.main:app --reload
```

### Windows (Git Bash)
```
# 가상 환경 활성화
source venv/Scripts/activate

# FastAPI 서버 실행
python -m uvicorn app.main:app --reload
```

### Mac/Linux (Bash/Zsh)
```
# 가상 환경 활성화
source venv/bin/activate

# FastAPI 서버 실행
python -m uvicorn app.main:app --reload
```
---

## 🗂️ Pinecone 벡터 DB 구조
### 1. 냉장고 재료 저장 (fridge 인덱스)
| 필드명  | 타입    | 설명                      |
|---------|--------|-------------------------|
| `id`    | string | 재료 이름 (예: "양파")       |
| `values` | list  | 벡터 값 (미사용, 빈 값 가능) |
| `metadata` | dict | 추가 정보 없음               |

### 2. 사용자 정보 및 음식 섭취 기록 (user_id 네임스페이스)
| 필드명  | 타입    | 설명                      |
|---------|--------|-------------------------|
| `id`    | string | 데이터 유형형 (예: "profile", "log_2025-03-10") |
| `values` | list  | 벡터 값 (미사용, 빈 값 가능) |
| `metadata` | dict | JSON 형태의 사용자 데이터   |

- 사용자 프로필
```
{
  "namespace": "dajeong",
  "id": "profile",
  "values": [],
  "metadata": {
    "age": 25,
    "preferred_foods": ["스테이크", "파스타", "해산물"],
    "disliked_foods": ["고수", "매운 음식"]
  }
}

```
- 음식 섭취 기록
```
{
  "namespace": "dajeong",
  "id": "log_2025-02-01",
  "values": [],
  "metadata": {
    "food": "파스타",
    "create_at": "2025-02-01T12:30:00"
  }
}
```
## 🔥 개발 중인 API
### 냉장고 재료 관리
- POST /llm/upcreate_inventory : 사용자의 냉장고에 재료 추가 또는 업데이트
- DELETE /inventory/remove_ingredient : 특정 재료 삭제

### 사용자 정보 및 음식 기록 관리
- PUT /llm/upcreate_user : 사용자 정보 업데이트 (나이, 좋아하는 음식, 싫어하는 음식 등)
- POST /llm/log_food : 음식 섭취 기록 저장

### 식단 및 메뉴 추천
- POST /llm/recommend_meal_plan : 사용자 그룹에 대한 주간 식단 추천
- POST /llm/recommend_menu : 특정 사용자에게 메뉴 추천