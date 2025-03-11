from pydantic import BaseModel
from typing import List
from datetime import datetime

class FoodLogEntry(BaseModel):
    member_id: str
    food: str
    date: datetime

class FoodLogRequest(BaseModel):
    logs: List[FoodLogEntry]
