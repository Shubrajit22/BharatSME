from fastapi import APIRouter, Depends, status
from app.modules.auth.schemas import UserCreate, UserLogin, Token, UserOut
from app.modules.auth.services import AuthService

router = APIRouter()

@router.post("/register", response_model=UserOut, status_code=status.HTTP_201_CREATED)
async def register(user_in: UserCreate):
    """Register a new SME Applicant."""
    return await AuthService.register_user(user_in)

@router.post("/login", response_model=Token)
async def login(user_in: UserLogin):
    """Login to receive a JWT access token."""
    return await AuthService.authenticate_user(user_in)