import base64
from fastapi import APIRouter, UploadFile, Form, File, Depends
from app.modules.kyc.schemas import BasicDetails, SubmitKyc
from app.modules.kyc import services

router = APIRouter()

@router.post("/basic-details")
async def check_duplicate_and_init(details: BasicDetails):
    return await services.init_kyc_session(details)

@router.post("/validate-pan")
async def validate_pan(
    applicationId: str = Form(...),
    panNumber: str = Form(...),
    file: UploadFile = File(...)
):
    image_bytes = await file.read()
    return await services.process_pan_validation(applicationId, panNumber, image_bytes)

@router.post("/validate-aadhaar")
async def validate_aadhaar(
    applicationId: str = Form(...),
    aadhaarNumber: str = Form(...),
    frontImage: UploadFile = File(...),
    backImage: UploadFile = File(...)
):
    front_bytes = await frontImage.read()
    back_bytes = await backImage.read()
    return await services.process_aadhaar_validation(applicationId, aadhaarNumber, front_bytes, back_bytes)

@router.post("/submit")
async def submit_kyc(data: SubmitKyc):
    # You can add a quick check here or move the check to services
    await services.update_kyc_status(data.applicationId, "PENDING")
    return {"success": True, "status": "PENDING"}