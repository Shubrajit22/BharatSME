import os
import json
import google.generativeai as genai
from fastapi import HTTPException
from dotenv import load_dotenv

# Load env variables (ensures API key is caught)
load_dotenv()
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

def auto_detect_model():
    """Dynamically finds the best available Gemini Vision model for your specific API key."""
    try:
        print("\n[System] Querying Google for your available AI models...")
        available_models = [m.name for m in genai.list_models() if 'generateContent' in m.supported_generation_methods]
        
        # Priority list of models (from newest/best to oldest)
        preferred_models = [
            'models/gemini-2.5-flash',
            'models/gemini-1.5-flash',
            'models/gemini-1.5-flash-latest',
            'models/gemini-1.5-pro',
            'models/gemini-pro-vision'
        ]
        
        for p_model in preferred_models:
            if p_model in available_models:
                print(f"[System] -> SUCCESS: Found and locked onto {p_model}")
                return p_model
                
        # Fallback if somehow none of the above are listed
        for m in available_models:
            if 'flash' in m or 'vision' in m:
                print(f"[System] -> SUCCESS: Using fallback model {m}")
                return m
                
        return "gemini-1.5-flash" # Absolute fallback
    except Exception as e:
        print(f"[System] -> Error fetching models: {e}")
        return "gemini-1.5-flash"

# Initialize the model dynamically!
SELECTED_MODEL = auto_detect_model()
model = genai.GenerativeModel(model_name=SELECTED_MODEL)

async def validate_document_with_ai(image_bytes: bytes, doc_type: str, expected_number: str, expected_name: str) -> dict:
    """
    Validates PAN or Aadhaar using Gemini Vision.
    """
    try:
        image_parts = [{"mime_type": "image/jpeg", "data": image_bytes}]

        prompt = f"""
        You are a highly strict banking compliance AI. Analyze the uploaded {doc_type} image.
        1. Verify it is a real, untampered {doc_type} card.
        2. Check if the image is clear and readable.
        3. Extract the document number and match it against: {expected_number}.
        4. Extract the name and check if it matches: {expected_name}.

        Respond strictly in JSON format with exactly this schema, nothing else:
        {{
            "isValid": true,
            "confidenceScore": 95,
            "extractedNumber": "string",
            "extractedName": "string",
            "reasonForRejection": null
        }}
        """

        response = model.generate_content([prompt, image_parts[0]])
        
        # Robust JSON cleaning (removes markdown backticks if the AI accidentally adds them)
        response_text = response.text.strip()
        if response_text.startswith("```"):
            lines = response_text.split('\n')
            response_text = '\n'.join(lines[1:-1]).strip()
            if response_text.endswith("```"):
                response_text = response_text[:-3]

        result = json.loads(response_text)
        return result

    except Exception as e:
        print(f"AI ERROR DURING GENERATION: {str(e)}")
        raise HTTPException(status_code=500, detail=f"AI Validation failed: {str(e)}")      