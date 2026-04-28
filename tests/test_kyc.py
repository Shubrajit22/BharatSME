import pytest
from unittest.mock import AsyncMock, patch, MagicMock

@pytest.mark.anyio
async def test_kyc_submission_success(client):
    payload = {
        "user_name": "Midanka Lahon",
        "email": "test@example.com",
        "document_type": "PAN",
        "document_id": "ABCDE1234F",
        "applicationId": "SME-2026-TEST"
    }

    with patch("app.modules.kyc.services.db") as mock_db:
        mock_application_actions = MagicMock()
        mock_db.kycapplication = mock_application_actions
        
        # 1. Create a mock object for the application record
        mock_application = MagicMock()
        mock_application.id = "SME-2026-TEST"
        mock_application.status = "PENDING"
        # 2. Add the fields your service is checking for
        mock_application.photoBase64 = "data:image/png;base64,mock_photo"
        mock_application.signatureBase64 = "data:image/png;base64,mock_sig"
        
        # 3. Tell the find_unique mock to return this object
        mock_application_actions.find_unique = AsyncMock(return_value=mock_application)
        mock_application_actions.update = AsyncMock(return_value=mock_application)

        response = await client.post("/api/v1/kyc/submit", json=payload)

        assert response.status_code == 201
        assert response.json()["status"] == "received"

@pytest.mark.anyio
async def test_kyc_submission_missing_field(client):
    """
    Test that missing required fields return 422.
    """
    incomplete_payload = {"user_name": "Midanka Lahon"}
    
    response = await client.post("/api/v1/kyc/submit", json=incomplete_payload)
    
    assert response.status_code == 422