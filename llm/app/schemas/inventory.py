from pydantic import BaseModel
from typing import List

class InventoryUpdateRequest(BaseModel):
    ingredients: List[str]

class InventoryDeleteRequest(BaseModel):
    ingredients: List[str]
