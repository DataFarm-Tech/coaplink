# server.py
import asyncio
import json
from aiocoap import resource, Context, Message, CHANGED, Code
from db.init import get_db
from db.db_models import SensorData
from sqlalchemy.exc import SQLAlchemyError

class CoAPResource(resource.Resource):
    async def render_post(self, request):
        payload = request.payload.decode()
        print("Received POST:", payload)

        try:
            data = json.loads(payload)
            sensor_id = data.get("sensor_id")
            value = data.get("value")
            unit = data.get("unit")

            if not sensor_id or value is None:
                return Message(code=Code.BAD_REQUEST, payload=b"Missing required fields")

            # Sync DB session in thread pool
            await asyncio.to_thread(self.save_to_db, sensor_id, value, unit)

            return Message(code=CHANGED, payload=b"OK")

        except json.JSONDecodeError:
            return Message(code=Code.BAD_REQUEST, payload=b"Invalid JSON")
        except Exception as e:
            print("Unexpected error:", e)
            return Message(code=Code.INTERNAL_SERVER_ERROR, payload=b"Internal error")

    def save_to_db(self, sensor_id, value, unit):
        db_gen = get_db()
        db = next(db_gen)
        try:
            new_entry = SensorData(sensor_id=sensor_id, value=value, unit=unit)
            db.add(new_entry)
            db.commit()
            print(f"Saved to DB: {sensor_id}, {value} {unit}")
        except SQLAlchemyError as e:
            db.rollback()
            print("DB Error:", e)
        finally:
            db_gen.close()

async def main():
    from db.db_models import Base
    from db.init import engine

    # Create tables if they don't exist
    Base.metadata.create_all(bind=engine)

    root = resource.Site()
    root.add_resource(('sensor',), CoAPResource())
    await Context.create_server_context(root)
    await asyncio.get_event_loop().create_future()  # run forever

if __name__ == "__main__":
    asyncio.run(main())
