import base64
# 1. Added BackgroundTasks here
from fastapi import APIRouter, UploadFile, Form, File, Depends, BackgroundTasks 
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

# 2. ADDED: The missing photo and signature upload route
@router.post("/upload-photo-signature")
async def upload_photo_signature(
    applicationId: str = Form(...),
    photo: UploadFile = File(...),
    signature: UploadFile = File(...)
):
    photo_bytes = await photo.read()
    signature_bytes = await signature.read()
    return await services.process_photo_signature_upload(applicationId, photo_bytes, signature_bytes)

# 3. UPDATED: Injected BackgroundTasks and pointed to the new service function
@router.post("/submit")
async def submit_kyc(data: SubmitKyc, background_tasks: BackgroundTasks):
    # This now passes the background_tasks to your service layer so it can send the email!
    return await services.finalize_kyc_submission(data.applicationId, background_tasks)