from app.core.guards import kyc_required
from fastapi import APIRouter, Depends, HTTPException
from app.modules.loans import services
from app.modules.loans.schemas import LoanCreate, LoanResponse
# Assuming you have a get_current_user dependency
from app.core.security import get_current_user 

router = APIRouter()

@router.post("/", response_model=LoanResponse)
async def create_app(data: LoanCreate, current_user = Depends(get_current_user)):
    return await services.create_loan_application(data, current_user.id)

@router.get("/", response_model=list[LoanResponse])
async def list_apps(current_user = Depends(get_current_user)):
    is_staff = current_user.role == "STAFF"
    return await services.get_user_applications(current_user.id, is_staff)

@router.post("/{id}/evaluate")
async def run_prescreen(id: str, current_user = Depends(get_current_user)):
    if current_user.role != "STAFF":
        raise HTTPException(status_code=403, detail="Only staff can run pre-screen")
    return await services.evaluate_eligibility(id)

@router.post("/apply")
async def apply_for_loan(
    data: LoanCreate, 
    # Just add this dependency to 'plug in' the KYC requirement
    user = Depends(kyc_required) 
):
    return await services.create_loan(data, user.id)