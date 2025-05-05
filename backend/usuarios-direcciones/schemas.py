from pydantic import BaseModel

class User(BaseModel):
    id: int
    firstname: str
    lastname: str
    phonenumber: str
    email: str
    age: int

class Address(BaseModel):
    user_id: int
    address_line: str
    city: str
    country: str

class Notification(BaseModel):
    user_id: int
    message: str
    timestamp: str  #formato: "YYYY-MM-DD HH:MM:SS"
    read: bool

class SupportTicket(BaseModel):
    user_id: int
    subject: str
    description: str
    status: str  #'open', 'in_progress', 'resolved'
    created_at: str  #formato: "YYYY-MM-DD HH:MM:SS"

