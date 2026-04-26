from pydantic import BaseModel, EmailStr
from datetime import datetime

class UserCreate(BaseModel):
    email: EmailStr
    password: str
    fullName: str

class UserLogin(BaseModel):
    # This can be the email or the APPLICANT-X username
    identifier: str 
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str

class UserOut(BaseModel):
    id: int
    username: str
    email: str
    fullName: str
    role: str
    createdAt: datetime

    class Config:
        from_attributes = True # Allows Prisma models to be converted to Pydantic