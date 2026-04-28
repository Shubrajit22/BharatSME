# Use the specific Python version requested
FROM python:3.10.11-slim

# Set environment variables to optimize Python performance
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set the working directory
WORKDIR /app

# Install system dependencies
# We keep openssl and ca-certificates for Prisma's binary engine
# libpq-dev is required if you are using psycopg2 for PostgreSQL
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libpq-dev \
    openssl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and Prisma schema first for layer caching
COPY requirements.txt .
COPY prisma/schema.prisma ./prisma/

# Install Python dependencies
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Generate the Prisma Client
# This creates the Python code needed to interact with your DB
RUN prisma generate

# Copy the rest of the application code
COPY . .

# Expose FastAPI port
EXPOSE 8000

# Command to run the application
# Removing --reload for a more "production-ready" Jenkins build
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]