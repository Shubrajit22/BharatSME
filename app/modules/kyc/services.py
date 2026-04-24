import base64
from fastapi import HTTPException
from app.core.database import db
from app.modules.kyc.schemas import BasicDetails
from app.modules.ai.services.ai_service import validate_document_with_ai

async def init_kyc_session(details: BasicDetails):
    existing = await db.kycapplication.find_first(where={"email": details.email})
    if existing:
        return {"isDuplicate": True, "applicationId": existing.id, "status": existing.status}
    
    new_app = await db.kycapplication.create(
        data={"fullName": details.fullName, "email": details.email, "status": "DRAFT"}
    )
    return {"isDuplicate": False, "applicationId": new_app.id}

async def process_pan_validation(app_id: str, pan_number: str, image_bytes: bytes):
    application = await db.kycapplication.find_unique(where={"id": app_id})
    if not application:
        raise HTTPException(status_code=404, detail="Application not found")
    if application.attempts >= 5:
        raise HTTPException(status_code=403, detail="Max verification attempts exceeded")

    ai_result = await validate_document_with_ai(
        image_bytes=image_bytes,
        doc_type="PAN Card",
        expected_number=pan_number,
        expected_name=application.fullName
    )
    
    if not ai_result.get("isValid"):
        await db.kycapplication.update(
            where={"id": app_id},
            data={"attempts": application.attempts + 1}
        )
        return {"success": False, "reason": ai_result.get("reasonForRejection"), "attemptsLeft": 4 - application.attempts}

    base64_img = base64.b64encode(image_bytes).decode('utf-8')
    await db.kycapplication.update(where={"id": app_id}, data={"panBase64": base64_img})
    return {"success": True, "message": "PAN verified successfully"}

async def process_aadhaar_validation(app_id: str, aadhaar_number: str, front_bytes: bytes, back_bytes: bytes):
    application = await db.kycapplication.find_unique(where={"id": app_id})
    if not application: raise HTTPException(status_code=404, detail="Application not found")

    ai_result = await validate_document_with_ai(
        image_bytes=front_bytes, doc_type="Aadhaar Card",
        expected_number=aadhaar_number, expected_name=application.fullName
    )

    if not ai_result.get("isValid"):
        await db.kycapplication.update(where={"id": app_id}, data={"attempts": application.attempts + 1})
        return {"success": False, "reason": ai_result.get("reasonForRejection")}

    await db.kycapplication.update(
        where={"id": app_id},
        data={
            "aadhaarFrontBase64": base64.b64encode(front_bytes).decode('utf-8'),
            "aadhaarBackBase64": base64.b64encode(back_bytes).decode('utf-8')
        }
    )
    return {"success": True, "message": "Aadhaar verified successfully"}

async def update_kyc_status(app_id: str, status: str):
    return await db.kycapplication.update(where={"id": app_id}, data={"status": status})