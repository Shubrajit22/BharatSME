from fastapi import HTTPException, status
from app.core.database import db
from app.core.security import get_password_hash, verify_password, create_access_token
from app.modules.auth.schemas import UserCreate, UserLogin, Token

class AuthService:
    @staticmethod
    async def register_user(user_in: UserCreate):
        # Check if email exists
        existing = await db.user.find_unique(where={"email": user_in.email})
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST, 
                detail="Email already registered"
            )

        # Logic for custom username (APPLICANT-X)
        user_count = await db.user.count()
        custom_username = f"APPLICANT-{user_count + 1}"

        # Create user in Prisma
        new_user = await db.user.create(
            data={
                "email": user_in.email,
                "username": custom_username,
                "password": get_password_hash(user_in.password),
                "fullName": user_in.fullName,
                "role": "APPLICANT"
            }
        )
        return new_user

    @staticmethod
    async def authenticate_user(user_in: UserLogin) -> Token:
        # Search for the user where either the email OR the username matches the identifier
        user = await db.user.find_first(
            where={
                "OR": [
                    {"email": user_in.identifier},
                    {"username": user_in.identifier}
                ]
            }
        )
        
        # Security check: Does user exist and does the password match?
        if not user or not verify_password(user_in.password, user.password):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid credentials",
                headers={"WWW-Authenticate": "Bearer"},
            )

        # Generate JWT using username as 'sub'
        access_token = create_access_token(data={"sub": user.username})
        
        return Token(
            access_token=access_token,
            token_type="bearer"
        )