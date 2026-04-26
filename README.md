# SME Loan and KYC Onboarding System

## Overview

The **SME Loan and KYC Onboarding System** is a high-performance, automated platform designed to streamline the lending process for Small and Medium Enterprises (SMEs). This system integrates advanced AI-driven KYC (Know Your Customer) verification with a robust loan pre-screening engine to ensure "Right-First-Time" application submissions.

Developed as an 8th-semester major project, the system emphasizes a modular architecture, secure document handling, and efficient biometric verification.

## Key Features

- **Automated KYC Processing**: Optimized OCR for Indian identification cards (Aadhaar, PAN) using a custom-tuned EasyOCR implementation.
- **Biometric Verification**: Face detection and recognition using `facenet-pytorch` for secure identity validation.
- **AI-Powered Extraction**: Standalone Python library for text extraction and embedding generation.
- **Vector Search & Analytics**: Utilization of `pgvector` in PostgreSQL for efficient storage and retrieval of identity embeddings.
- **Encrypted Document Storage**: Secure object storage for sensitive documents using MinIO.
- **Microservices-Ready Backend**: A high-performance API built with FastAPI, optimized for asynchronous processing.
- **Native Android Client**: A seamless onboarding experience developed with Kotlin and Jetpack Compose.

## Technical Architecture

### Backend & AI

- **Framework**: FastAPI (Python)
- **AI/ML Stack**: PyTorch, EasyOCR, facenet-pytorch
- **Dependency Management**: `pip-compile` targeting CUDA 12.5/cu124
- **Task Queue/Caching**: Redis

### Data & Storage

- **Primary Database**: PostgreSQL with the `pgvector` extension
- **Object Storage**: MinIO (S3-compatible, encrypted document storage)
- **Secrets Management**: Infisical Cloud

### Frontend

- **Platform**: Native Android
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)

## Project Structure

```text
├── ai-library/          # Standalone AI logic for OCR & Embeddings
├── backend/             # FastAPI application services
├── android-app/         # Kotlin/Jetpack Compose source code
├── docker-compose.yml   # Infrastructure orchestration
└── requirements.in      # Base dependency definitions
```

## Setup and Installation

### 1. Environment Preparation

```bash
python -m venv venv
```

### 2. Dependency Management

```bash
python -m pip install pip-tools
python -m piptools compile requirements.in
```

### 3. Synchronize Dependencies

```bash
python -m piptools sync requirements.txt
```

### 4. Running the Application

```bash
python -m uvicorn app.main:app --reload
```

## Design Decisions

- **Standalone AI Module**: The OCR and Biometric logic is decoupled from the main API to allow for independent scaling and easier testing.
- **Vectorized Identity**: Storing facial embeddings in `pgvector` enables rapid identity deduplication and fraud detection at the database level.
- **Hybrid Storage Strategy**: Metadata is managed in PostgreSQL, while heavy files are offloaded to MinIO for optimal performance and encryption.
