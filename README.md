# BharatSME: SME Loan & Digital KYC API

BharatSME is a modern, modular FastAPI-based backend system designed to streamline the loan application process for Small and Medium Enterprises (SMEs). It features a fully automated Digital KYC system powered by Google Gemini AI Vision for document verification.

## 🚀 Features

### 🔐 Authentication & Security
- **JWT-based Auth**: Secure user registration and login.
- **Role-Based Access Control (RBAC)**: Supports `APPLICANT`, `STAFF`, and `ADMIN` roles.
- **Secure Password Hashing**: Uses `bcrypt` for user security.

### 🆔 Automated Digital KYC
- **Custom AI Engine**: Uses a **Proprietary Trained OCR Model** and **Face Recognition** (FaceNet) for high-accuracy document verification and biometric matching.
- **OCR & Extraction**: Automatically extracts sensitive data from PAN and Aadhaar images with custom-tuned weights.
- **Biometric Matching**: Compares the applicant's live photo with the ID document photo to ensure identity integrity.
- **Session Management**: Multi-step KYC process including basic details, document uploads, and photo/signature capture.
- **Background Tasks**: Automated email confirmations sent upon KYC submission.

### 💰 SME Loan Management
- **Streamlined Applications**: SME owners can apply for loans by providing business details and turnover bands.
- **Eligibility Pre-screening**: Automated checks for business age and turnover requirements.
- **Staff Review**: Specialized dashboard endpoints for staff to evaluate and process applications.
- **KYC Guard**: Integrated middleware ensures only KYC-verified users can apply for loans.

### 📧 Email Integration
- **Automated Notifications**: Beautifully styled HTML emails sent via SMTP for application status updates.

## 🛠 Tech Stack

- **Framework**: [FastAPI](https://fastapi.tiangolo.com/) (Python 3.10+)
- **ORM**: [Prisma Client Python](https://prisma-client-py.readthedocs.io/)
- **Database**: PostgreSQL
- **AI Engine**: Custom Trained OCR Model & Face Recognition (FaceNet/OpenCV)
- **Generative AI**: [Google Generative AI (Gemini Flash)](https://ai.google.dev/) for intelligent verification logic.
- **Security**: JWT (python-jose), Bcrypt
- **Validation**: Pydantic v2
- **Environment**: python-dotenv

## 📁 Project Structure

```text
app/
├── api/v1/             # Versioned API route definitions
├── core/               # Core configs (DB, Security, Guards, Middleware)
├── modules/            # Domain-driven feature modules
│   ├── ai/             # Gemini AI integration services
│   ├── auth/           # User authentication & registration
│   ├── kyc/            # KYC application & AI validation logic
│   ├── loans/          # SME Loan application & processing
│   ├── email/          # SMTP services for notifications
│   └── documents/      # (Internal) Document handling
└── shared/             # Shared exceptions and utilities
prisma/                 # Prisma schema and migrations
```

## ⚙️ Setup & Installation

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd BharatSME
```

### 2. Create a Virtual Environment
```bash
python -m venv venv
# Windows
.\venv\Scripts\activate
# Linux/macOS
source venv/bin/activate
```

### 3. Install Dependencies
This project uses `pip-tools` for dependency management.
```bash
# Install pip-tools first
pip install pip-tools

# Compile requirements from .in file
python -m piptools compile .\requirements.in

# Sync your environment with the requirements.txt
python -m piptools sync .\requirements.txt
```

### 4. Environment Configuration
Create a `.env` file in the root directory:
```env
DATABASE_URL="postgresql://user:password@localhost:5432/bharatsme"
GEMINI_API_KEY="your_google_gemini_api_key"
SECRET_KEY="your_jwt_secret_key"
ALGORITHM="HS256"

# Email Settings
SMTP_SERVER="smtp.gmail.com"
SMTP_PORT=587
SMTP_USERNAME="your-email@gmail.com"
SMTP_PASSWORD="your-app-password"
```

### 5. Database Setup (Prisma)
```bash
# Generate Prisma Client
prisma generate

# Run migrations
prisma db push
```

## 🏃 Running the Application

```bash
uvicorn app.main:app --reload
```
Once started, you can access the interactive API documentation at:
- **Swagger UI**: [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)
- **ReDoc**: [http://127.0.0.1:8000/redoc](http://127.0.0.1:8000/redoc)

## 📡 API Endpoints Reference

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register a new SME Applicant account |
| POST | `/login` | Authenticate and receive JWT access token |

### Digital KYC (`/api/v1/kyc`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/basic-details` | Initialize KYC session and check for existing applications |
| POST | `/validate-pan` | Upload PAN image and validate via Gemini AI |
| POST | `/validate-aadhaar` | Upload Aadhaar (Front/Back) and validate via Gemini AI |
| POST | `/upload-photo-signature` | Upload applicant's live photo and digital signature |
| POST | `/submit` | Finalize KYC application and trigger confirmation email |

### SME Loans (`/api/v1/loans`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create a new loan application (Authenticated) |
| GET | `/` | List applications (Applicants see theirs; Staff see all) |
| POST | `/apply` | Protected route that requires completed KYC to apply |
| POST | `/{id}/evaluate` | **[Staff Only]** Trigger AI-driven eligibility evaluation |

### System
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | API health check and version info |

## 🚢 Deployment & CI/CD

The project is designed for modern cloud-native deployment with the following stack:

- **CI/CD Pipeline**: [Jenkins](https://www.jenkins.io/) is used for automated testing, building, and deployment workflows.
- **Containerization**: [Docker](https://www.docker.com/) is used to package the application and its dependencies into a single immutable image.
- **Orchestration**: [Docker Compose](https://docs.docker.com/compose/) is used for local development and simple production orchestration of the API and PostgreSQL services.

---
*Developed for BharatSME - Bridging the gap for SME Financing.*
