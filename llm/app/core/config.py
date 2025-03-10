# 환경 변수 및 설정 파일

import os
from pinecone import Pinecone
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()

PINECONE_API_KEY = os.getenv("PINECONE_API_KEY")
PINECONE_ENV = os.getenv("PINECONE_ENV")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

print(f"✅ PINECONE_API_KEY: {PINECONE_API_KEY[:5]}****")
print(f"✅ PINECONE_ENV: {PINECONE_ENV}")

# 디버깅: API 키 확인
if not PINECONE_API_KEY:
    raise ValueError("🔴 PINECONE_API_KEY 환경 변수가 설정되지 않았습니다. .env 파일을 확인하세요.")

# Pinecone 인스턴스 생성
pc = Pinecone(api_key=PINECONE_API_KEY)

# 현재 생성된 인덱스 확인
print("📌 Pinecone 인덱스 목록:", pc.list_indexes().names())