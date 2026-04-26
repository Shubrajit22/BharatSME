from pydantic import BaseModel

class BasicDetails(BaseModel):
    fullName: str
    email: str

class SubmitKyc(BaseModel):
    applicationId: str