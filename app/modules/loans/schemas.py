from pydantic import BaseModel
from decimal import Decimal
from datetime import datetime
from typing import Optional
from enum import Enum

class LoanCreate(BaseModel):
    applicantName: str
    businessType: str
    turnoverBand: str
    requestedLoanAmount: Decimal
    yearsInBusiness: int

class LoanResponse(LoanCreate):
    id: str
    eligibilityStatus: str
    preScreenResult: str
    lockedByStaff: bool
    createdAt: datetime
    updatedAt: datetime

    class Config:
        from_attributes = True