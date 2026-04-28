import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app
from app.core.database import db

@pytest.fixture(scope="session")
def anyio_backend():
    return "asyncio"

@pytest.fixture(scope="session", autouse=True)
async def setup_db():
    if not db.is_connected():
        await db.connect()
    yield
    if db.is_connected():
        await db.disconnect()

@pytest.fixture
async def client():
    transport = ASGITransport(app=app)
    # We use the root as the base_url to keep the tests explicit
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        yield ac