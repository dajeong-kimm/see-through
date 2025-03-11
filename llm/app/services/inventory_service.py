from app.db.pinecone_client import index
from app.schemas.inventory import InventoryDeleteRequest
from app.schemas.inventory import InventoryUpdateRequest
from fastapi import HTTPException
from app.utils.embedding import get_embedding  # ✅ 임베딩 함수 사용
import base64
import logging

# 로깅 설정
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def encode_to_ascii(text: str) -> str:
    """한글 포함된 문자열을 ASCII 문자열로 변환"""
    return base64.urlsafe_b64encode(text.encode()).decode()

def upsert_inventory(ingredients: InventoryUpdateRequest):
    """냉장고 재료를 Pinecone DB에 저장 또는 업데이트"""

    namespace = "fridge"  # 냉장고 전용 네임스페이스

    vectors = []

    for ingredient in ingredients.ingredients:
        ingredient_id = encode_to_ascii(ingredient)  # 재료명을 ASCII ID로 변환
        ingredient_vector = get_embedding(ingredient)  # 재료명 임베딩 벡터 생성

        metadata = {
            "ingredient": ingredient
        }

        # Pinecone에 저장할 벡터 (재료명 임베딩 적용)
        vectors.append((ingredient_id, ingredient_vector, metadata))

    # Pinecone에 저장 (namespace="fridge")
    index.upsert(vectors, namespace=namespace)


def delete_inventory(ingredients: InventoryDeleteRequest):
    """냉장고 재료를 Pinecone DB에서 삭제"""

    namespace = "fridge"
    ingredient_ids = [encode_to_ascii(ingredient) for ingredient in ingredients.ingredients]

    # Pinecone에서 기존 재료 조회
    fetch_response = index.fetch(ids=ingredient_ids, namespace=namespace)

    existing_ingredient_ids = fetch_response.vectors.keys()

    # 존재하지 않는 재료 필터링
    missing_ingredients = [ing for ing in ingredients.ingredients if encode_to_ascii(ing) not in existing_ingredient_ids]

    if missing_ingredients:
        raise HTTPException(status_code=404, detail=f"일부 재료를 찾을 수 없습니다: {missing_ingredients}")

    # Pinecone에서 재료 삭제
    index.delete(ids=ingredient_ids, namespace=namespace)
