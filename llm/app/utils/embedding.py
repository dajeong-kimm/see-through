# OpenAI 임베딩 변환 

from langchain_openai import OpenAIEmbeddings
import os
from dotenv import load_dotenv

# ✅ 환경 변수 로드
load_dotenv()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

if not OPENAI_API_KEY:
    raise ValueError("🔴 `OPENAI_API_KEY` 환경 변수가 설정되지 않았습니다.")

# ✅ OpenAI 임베딩 모델 로드
embedding_model = OpenAIEmbeddings(openai_api_key=OPENAI_API_KEY, model="text-embedding-3-small")

def get_embedding(text: str):
    """텍스트를 벡터로 변환"""
    return embedding_model.embed_query(text)
