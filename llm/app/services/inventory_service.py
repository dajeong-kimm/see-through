from app.db.pinecone_client import index
from app.schemas.inventory import InventoryUpdateRequest
from app.utils.embedding import get_embedding  # ✅ 임베딩 함수 사용
import base64

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
