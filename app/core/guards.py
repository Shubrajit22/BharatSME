from fastapi import Depends, HTTPException, status
from app.core.security import get_current_user
from app.modules.kyc.services import is_user_kyc_verified

async def kyc_required(current_user = Depends(get_current_user)):
    """
    A reusable 'Guard' that can be plugged into any route.
    """
    verified = await is_user_kyc_verified(current_user.email)
    if not verified:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="KYC verification required to access this resource"
        )
    return current_user