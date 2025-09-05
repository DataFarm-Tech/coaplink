# server.py
import asyncio
from aiocoap import resource, Context, Message, CHANGED

class CoAPResource(resource.Resource):
    async def render_post(self, request):
        print("Received POST:", request.payload.decode())
        # Save to file, DB, etc.
        return Message(code=CHANGED, payload=b"OK")

async def main():
    root = resource.Site()
    root.add_resource(('sensor',), CoAPResource())
    await Context.create_server_context(root)
    await asyncio.get_event_loop().create_future()  # run forever

if __name__ == "__main__":
    asyncio.run(main())