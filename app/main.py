from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.v1.api import api_router as v1_router
from app.database import connect_db, disconnect_db
from contextlib import asynccontextmanager

@asynccontextmanager
async def lifespan(app: FastAPI):
    await connect_db()
    yield
    await disconnect_db()

app = FastAPI(
    title="SME Loan & KYC API",
    version="1.0.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Versioned Routes
app.include_router(v1_router, prefix="/api/v1")

# If you ever have a V2, it's as simple as:
# app.include_router(v2_router, prefix="/api/v2")

@app.get("/")
async def root():
    return {"message": "SME Loan API is live", "version": "v1"}