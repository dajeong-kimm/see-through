from typing import List, Dict, Optional
from pydantic import BaseModel

class Meal(BaseModel):
    serving_time: str  # "BREAKFAST", "LUNCH", "DINNER"
    menu: List[str]

class DailyMealPlan(BaseModel):
    date: str
    meals: List[Meal]

class RecommendMealPlanRequest(BaseModel):
    member_ids: List[str]
    day_count: int
    schedule: List[Dict]
    additional_request: Optional[str] = None

class RecommendMealPlanResponse(BaseModel):
    day_count: int
    daily_meal_plans: List[DailyMealPlan]
