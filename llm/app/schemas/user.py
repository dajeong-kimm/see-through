from pydantic import BaseModel
from typing import List

class UserProfile(BaseModel):
    member_id: str
    age: int
    preferred_foods: List[str]
    disliked_foods: List[str]
