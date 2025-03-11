from pydantic import BaseModel
from typing import Optional

class RecommendMenuRequest(BaseModel):
    member_id: str 
    additional_request: Optional[str] = None

class RecommendMenuResponse(BaseModel):
    recommended_menu: str
