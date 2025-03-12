# 🍽️ FastAPI 기반 냉장고 & 식단 관리 API

## 🚀 실행 방법
### 1. 가상 환경 생성
```
py -3.10 -m venv venv
```
### 2. 가상 환경 활성화 & 패키지 설치
- Windows (PowerShell)
```
# 가상 환경 활성화
.\venv\Scripts\Activate

# requirements.txt 설치
pip install -r requirements.txt
```
- Windows (CMD)
```
# 가상상 환경 활성화
venv\Scripts\activate.bat

# requirements.txt 설치
pip install -r requirements.txt
```
- Mac/Linux
```
# 가상상 환경 활성화
source venv/bin/activate

# requirements.txt 설치
pip install -r requirements.txt
```
### 3. FastAPI 서버 실행행
```
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