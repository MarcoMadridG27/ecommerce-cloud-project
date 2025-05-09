from pydantic import BaseModel
from typing import Optional

class User(BaseModel):
    id: Optional[int] = None  
    firstname: str
    lastname: str
    phonenumber: str
    email: str
    age: int
    password: str

class Address(BaseModel):
    user_id: int
    address_line: str
    city: str
    country: str

class Notification(BaseModel):
    user_id: int
    message: str
    timestamp: str  
    read: bool

class SupportTicket(BaseModel):
    user_id: int
    subject: str
    description: str
    status: str  
    created_at: str  

class LoginData(BaseModel):
    email: str
    password: str
