import base64
from contextlib import asynccontextmanager
from fastapi import FastAPI, UploadFile, Form, File, HTTPException, Depends, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from prisma import Prisma
from services.ai_service import validate_document_with_ai
from services.email_service import send_kyc_confirmation_email
# Initialize Prisma
db = Prisma()

@asynccontextmanager
async def lifespan(app: FastAPI):
    await db.connect()
    yield
    await db.disconnect()

app = FastAPI(lifespan=lifespan, title="Digital KYC API")

# Configure CORS for your Next.js frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Pydantic Models ---
class BasicDetails(BaseModel):
    fullName: str
    email: str

class SubmitKyc(BaseModel):
    applicationId: str    

# --- Endpoints ---

@app.post("/api/kyc/basic-details")
async def check_duplicate_and_init(details: BasicDetails):
    """Checks for duplicates and initializes a KYC session."""
    existing_user = await db.kycapplication.find_first(
        where={"email": details.email}
    )
    
    if existing_user:
        return {
            "isDuplicate": True, 
            "applicationId": existing_user.id,
            "status": existing_user.status
        }
        
    new_app = await db.kycapplication.create(
        data={
            "fullName": details.fullName,
            "email": details.email,
            "status": "DRAFT"
        }
    )
    return {"isDuplicate": False, "applicationId": new_app.id}


@app.post("/api/kyc/validate-pan")
async def validate_pan(
    applicationId: str = Form(...),
    panNumber: str = Form(...),
    file: UploadFile = File(...)
):
    """Validates PAN via Gemini and stores Base64 if successful."""
    application = await db.kycapplication.find_unique(where={"id": applicationId})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")
        
    if application.attempts >= 5:
        raise HTTPException(status_code=403, detail="Maximum verification attempts exceeded.")

    image_bytes = await file.read()
    
    # Run AI Validation
    ai_result = await validate_document_with_ai(
        image_bytes=image_bytes,
        doc_type="PAN Card",
        expected_number=panNumber,
        expected_name=application.fullName
    )
    
    if not ai_result.get("isValid"):
        await db.kycapplication.update(
            where={"id": applicationId},
            data={"attempts": application.attempts + 1}
        )
        return {"success": False, "reason": ai_result.get("reasonForRejection"), "attemptsLeft": 4 - application.attempts}

    # Convert to Base64 for secure storage
    base64_img = base64.b64encode(image_bytes).decode('utf-8')
    
    await db.kycapplication.update(
        where={"id": applicationId},
        data={"panBase64": base64_img}
    )
    
    return {"success": True, "message": "PAN verified successfully"}


@app.post("/api/kyc/validate-aadhaar")
async def validate_aadhaar(
    applicationId: str = Form(...),
    aadhaarNumber: str = Form(...),
    frontImage: UploadFile = File(...),
    backImage: UploadFile = File(...)
):
    """Validates Aadhaar via Gemini and stores Base64."""
    application = await db.kycapplication.find_unique(where={"id": applicationId})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    front_bytes = await frontImage.read()
    back_bytes = await backImage.read()

    # AI Validation (Validating front image for Name and Number)
    ai_result = await validate_document_with_ai(
        image_bytes=front_bytes,
        doc_type="Aadhaar Card",
        expected_number=aadhaarNumber,
        expected_name=application.fullName
    )

    if not ai_result.get("isValid"):
        await db.kycapplication.update(
            where={"id": applicationId},
            data={"attempts": application.attempts + 1}
        )
        return {"success": False, "reason": ai_result.get("reasonForRejection")}

    # Store both sides
    front_b64 = base64.b64encode(front_bytes).decode('utf-8')
    back_b64 = base64.b64encode(back_bytes).decode('utf-8')

    await db.kycapplication.update(
        where={"id": applicationId},
        data={
            "aadhaarFrontBase64": front_b64,
            "aadhaarBackBase64": back_b64
        }
    )
    
    return {"success": True, "message": "Document verified successfully"}

@app.post("/api/kyc/upload-photo-signature")
async def upload_photo_signature(
    applicationId: str = Form(...),
    photo: UploadFile = File(...),
    signature: UploadFile = File(...)
):
    """Handles the raw file uploads for the user's live photo and signature."""
    application = await db.kycapplication.find_unique(where={"id": applicationId})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    # Read the incoming files
    photo_bytes = await photo.read()
    signature_bytes = await signature.read()

    # Convert to Base64 for database storage
    photo_b64 = base64.b64encode(photo_bytes).decode('utf-8')
    signature_b64 = base64.b64encode(signature_bytes).decode('utf-8')

    # Update the database
    await db.kycapplication.update(
        where={"id": applicationId},
        data={
            "photoBase64": photo_b64,
            "signatureBase64": signature_b64
        }
    )
    
    return {"success": True, "message": "Photo and signature uploaded successfully"}
# 1. The background processor function is defined FIRST
async def mock_kyc_processing(application_id: str):
    import asyncio
    await asyncio.sleep(5)
    await db.kycapplication.update(where={"id": application_id}, data={"status": "APPROVED"})
    print(f"[System] Application {application_id} has been APPROVED.")


# 2. Then the submit endpoint is defined BELOW it
@app.post("/api/kyc/submit")
async def submit_kyc(data: SubmitKyc, background_tasks: BackgroundTasks):
    """Finalizes the KYC upload and triggers background processing & emails."""
    
    # We MUST fetch the application first so we know who to email!
    application = await db.kycapplication.find_unique(where={"id": data.applicationId})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")

    # Update status to PENDING
    updated_app = await db.kycapplication.update(
        where={"id": data.applicationId},
        data={"status": "PENDING"}
    )
    
    # Trigger the simulated processing
    background_tasks.add_task(mock_kyc_processing, application_id=data.applicationId)
    
    # Trigger the email dispatch
    background_tasks.add_task(
        send_kyc_confirmation_email, 
        recipient_email=application.email, 
        user_name=application.fullName, 
        application_id=application.id
    )
    
    return {"success": True, "status": updated_app.status}
@app.get("/api/kyc/status/{application_id}")
async def get_kyc_status(application_id: str):
    """Endpoint for Next.js frontend to poll the current status."""
    application = await db.kycapplication.find_unique(where={"id": application_id})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")
        
    return {"applicationId": application.id, "status": application.status}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)