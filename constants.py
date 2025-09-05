import os
from dotenv import load_dotenv

script_dir = os.path.dirname(__file__)
dotenv_path = os.path.join(script_dir, '..', '.env')

if not os.getenv('GITHUB_ACTIONS'):
    load_dotenv()
else:
    DB_USER: str = os.getenv('DB_USER')
    DB_PASSWORD: str = os.getenv('DB_PASSWORD')
    IPV4: str = os.getenv('IPV4')
    PORT_NUMBER: int = os.getenv('PORT_NUMBER')
    DB_DATABASE: str = os.getenv('DB_DATABASE')

DB_USER: str = os.getenv('DB_USER')
DB_PASSWORD: str = os.getenv('DB_PASSWORD')
IPV4: str = os.getenv('IPV4')
PORT_NUMBER: int = os.getenv('PORT_NUMBER')
DB_DATABASE: str = os.getenv('DB_DATABASE')