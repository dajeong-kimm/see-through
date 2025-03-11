# 음식 섭취 기록 저장 서비스

from datetime import datetime
from app.db.pinecone_client import index
from app.utils.embedding import get_embedding
from typing import List
import base64

def encode_to_ascii(text: str) -> str:
    """한글 포함된 문자열을 ASCII 문자열로 변환"""
    return base64.urlsafe_b64encode(text.encode()).decode()

def save_to_pinecone_bulk(data_list: List[dict]):
    """여러 개의 텍스트 데이터를 한 번에 벡터로 변환 후 Pinecone에 저장"""
    vectors = []

    for data in data_list:
        text = data["text"]
        metadata = data["metadata"]
        data_id = encode_to_ascii(data["data_id"])  # ASCII 변환
        namespace = data["namespace"]

        # datetime을 문자열로 변환
        if isinstance(metadata["date"], datetime):
            metadata["date"] = metadata["date"].isoformat()

        vector = get_embedding(text)
        vectors.append((data_id, vector, metadata))

    # Pinecone에 배치 업로드
    index.upsert(vectors, namespace=namespace)

