from decimal import Decimal
from fastapi import HTTPException
from app.core.database import db
from app.modules.kyc.services import is_user_kyc_verified
from app.modules.loans.schemas import LoanCreate

async def create_loan_application(data: LoanCreate, owner_id: int):
    return await db.smeapplication.create(
        data={
            **data.model_dump(),
            "ownerId": owner_id,
            "eligibilityStatus": "PENDING",
            "preScreenResult": "BLOCKED_MISSING_DOCS"
        }
    )

async def evaluate_eligibility(app_id: str):
    application = await db.smeapplication.find_unique(where={"id": app_id})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    reasons = []
    status = "ELIGIBLE"

    # Business Age Rule
    if application.yearsInBusiness < 1:
        status = "INELIGIBLE"
        reasons.append("BUSINESS_TOO_NEW")

    # Minimum Loan Amount Rule (50,000)
    if application.requestedLoanAmount < Decimal("50000"):
        status = "INELIGIBLE"
        reasons.append("LOAN_AMOUNT_TOO_LOW")

    await db.smeapplication.update(
        where={"id": app_id},
        data={"eligibilityStatus": status}
    )
    
    return {"status": status, "reasons": reasons}

async def get_user_applications(user_id: int, is_staff: bool):
    if is_staff:
        return await db.smeapplication.find_many()
    return await db.smeapplication.find_many(where={"ownerId": user_id})

async def evaluate_loan_prescreen(application_id: str):
    application = await db.smeapplication.find_unique(
        where={"id": application_id},
        include={"user": True} # Fetch the user to get their email
    )
    
    # Plug in the KYC check
    kyc_verified = await is_user_kyc_verified(application.user.email)
    
    if not kyc_verified:
        # Rule 1 from your Java code: Auto-block if KYC is missing
        await db.smeapplication.update(
            where={"id": application_id},
            data={"preScreenResult": "BLOCKED_INELIGIBLE"}
        )
        return {"ready": False, "reason": "KYC_INCOMPLETE"}