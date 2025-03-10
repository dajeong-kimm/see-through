# pinecone 클라이언트 설정

from app.core.config import pc, INDEX_NAME

# Pinecone 인덱스 가져오기
index = pc.Index(INDEX_NAME)
