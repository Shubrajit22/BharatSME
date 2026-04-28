# Use the specific Python version requested
FROM python:3.10.11-slim

# Set environment variables to optimize Python performance
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set the working directory
WORKDIR /app

# Install system dependencies
# Prisma requires openssl and ca-certificates to download/run its binary engine
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libpq-dev \
    openssl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and Prisma schema first for layer caching
# Note: Assuming your schema is in a 'prisma' directory
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
# Prisma doesn't require a special start command, but you might want 
# to run migrations before starting in a production environment
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]