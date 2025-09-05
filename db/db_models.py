from sqlalchemy import Column, Integer, String, Float, DateTime
from sqlalchemy.sql import func
from db.init import Base

class SensorData(Base):
    __tablename__ = "sensor_data"

    id = Column(Integer, primary_key=True, index=True)
    sensor_id = Column(String(100), nullable=False)
    value = Column(Float, nullable=False)
    unit = Column(String(20), nullable=True)
    timestamp = Column(DateTime(timezone=True), server_default=func.now())