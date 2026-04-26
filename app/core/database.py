from prisma import Prisma
import logging

# Initialize the Prisma client
# auto_register=True allows you to use the client without passing it explicitly to every model
db = Prisma(auto_register=True)

async def connect_db():
    """
    Establishes the connection to PostgreSQL.
    This should be called during the FastAPI startup 'lifespan'.
    """
    try:
        await db.connect()
        logging.info("Successfully connected to the database.")
    except Exception as e:
        logging.error(f"Error connecting to the database: {e}")
        raise e

async def disconnect_db():
    """
    Gracefully closes the database connection.
    This should be called during the FastAPI shutdown 'lifespan'.
    """
    if db.is_connected():
        await db.disconnect()
        logging.info("Database connection closed.")