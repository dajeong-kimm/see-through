# FastAPI 서버 실행
from fastapi import FastAPI
from app.api.food_log import router as food_log_router
from app.api.user import router as user_router
from app.api.inventory import router as inventory_router
from app.api.personal_notice import router as personal_notice_router

app = FastAPI()

# ✅ API 라우터 등록
app.include_router(food_log_router, prefix="/llm")
app.include_router(user_router, prefix="/llm")
app.include_router(inventory_router, prefix="/llm")
app.include_router(personal_notice_router, prefix="/llm")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
