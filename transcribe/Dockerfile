# Stage 1: Build dependencies
FROM python:3.11-slim AS builder

# Set working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    libasound2 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
RUN pip install --no-cache-dir flask pydub azure-cognitiveservices-speech

# Copy application files (optional if dependencies are pre-built)
COPY . .

# Stage 2: Runtime environment
FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Copy only required files from builder stage
COPY --from=builder /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin
COPY . .

# Install system dependencies (if runtime needs them)
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    libasound2 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Expose the port Flask will run on
EXPOSE 5000

# Command to run the application
CMD ["python", "SpeechToText.py"]
