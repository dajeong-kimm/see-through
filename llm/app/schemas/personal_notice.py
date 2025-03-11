from pydantic import BaseModel
from datetime import datetime

class PersonalNoticeRequest(BaseModel):
    member_id: str
    removed_ingredient: str
    date: datetime

class PersonalNoticeResponse(BaseModel):
    notice_message: str
