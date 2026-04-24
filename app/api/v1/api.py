from fastapi import APIRouter
from app.modules.kyc.router import router as kyc_router
# from app.modules.auth.router import router as auth_router (Add later)

api_router = APIRouter()

# Register module routers under V1
api_router.include_router(kyc_router, prefix="/kyc", tags=["KYC"])
# api_router.include_router(auth_router, prefix="/auth", tags=["Auth"])