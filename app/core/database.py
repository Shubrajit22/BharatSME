from prisma import Prisma

db = Prisma(auto_register=True)

async def connect_db():
    await db.connect()

async def disconnect_db():
    await db.disconnect()