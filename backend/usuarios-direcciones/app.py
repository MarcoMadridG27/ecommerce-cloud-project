from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import mysql.connector
import schemas

app = FastAPI()

origins = ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

db_config = {
    "host": "172.31.22.204",  # IP privada de tu instancia MySQL en AWS
    "port": 8005,
    "user": "root",
    "password": "utec",
    "database": "bd_api_employees"
}

def get_db():
    return mysql.connector.connect(**db_config)

@app.get("/")
def echo():
    return {"message": "Echo Test OK"}

@app.get("/users")
def get_users():
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Users")
    result = cursor.fetchall()
    db.close()
    return {"users": result}

@app.post("/users")
def add_user(user: schemas.User):
    db = get_db()
    cursor = db.cursor()
    sql = "INSERT INTO Users (firstname, lastname, phonenumber, email, age) VALUES (%s, %s, %s, %s, %s)"
    cursor.execute(sql, (user.firstname, user.lastname, user.phonenumber, user.email, user.age))
    db.commit()
    db.close()
    return {"message": "User created successfully"}

@app.get("/users/{id}")
def get_user(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Users WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    return {"user": result}

@app.put("/users/{id}")
def update_user(id: int, user: schemas.User):
    db = get_db()
    cursor = db.cursor()
    sql = "UPDATE Users SET firstname=%s, lastname=%s, phonenumber=%s, email=%s, age=%s WHERE id=%s"
    cursor.execute(sql, (user.firstname, user.lastname, user.phonenumber, user.email, user.age, id))
    db.commit()
    db.close()
    return {"message": "User updated successfully"}

@app.delete("/users/{id}")
def delete_user(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("DELETE FROM Users WHERE id=%s", (id,))
    db.commit()
    db.close()
    return {"message": "User deleted successfully"}

@app.get("/addresses")
def get_adresses():
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Addresses")
    result = cursor.fetchall()
    db.close()
    return {"addresses": result}

@app.post("/addresses")
def add_adress(adress: schemas.Address):
    db = get_db()
    cursor = db.cursor()
    sql = "INSERT INTO Addresses (user_id, address_line, city, country) VALUES (%s, %s, %s, %s)"
    cursor.execute(sql, (adress.user_id, adress.address_line, adress.city, adress.country))
    db.commit()
    db.close()
    return {"message": "Adress created successfully"}

@app.get("/addresses/{id}")
def get_adress(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Addresses WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    return {"adress": result}

@app.put("/addresses/{id}")
def update_adress(id: int, adress: schemas.Address):
    db = get_db()
    cursor = db.cursor()
    sql = "UPDATE Addresses SET user_id=%s, address_line=%s, city=%s, country=%s WHERE id=%s"
    cursor.execute(sql, (adress.user_id, adress.address_line, adress.city, adress.country, id))
    db.commit()
    db.close()
    return {"message": "Adress updated successfully"}

@app.delete("/addresses/{id}")
def delete_adress(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("DELETE FROM Addresses WHERE id=%s", (id,))
    db.commit()
    db.close()
    return {"message": "Adress deleted successfully"}

@app.get("/notifications")
def get_notifications():
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Notifications")
    result = cursor.fetchall()
    db.close()
    return {"notifications": result}

@app.post("/notifications")
def add_notification(notification: schemas.Notification):
    db = get_db()
    cursor = db.cursor()
    sql = "INSERT INTO Notifications (user_id, message, timestamp, read) VALUES (%s, %s, %s, %s)"
    cursor.execute(sql, (notification.user_id, notification.message, notification.timestamp, notification.read))
    db.commit()
    db.close()
    return {"message": "Notification created successfully"}


@app.get("/notifications/{id}")
def get_notification(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM Notifications WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    return {"notification": result}

@app.put("/notifications/{id}")
def update_notification(id: int, notification: schemas.Notification):
    db = get_db()
    cursor = db.cursor()
    sql = "UPDATE Notifications SET user_id=%s, message=%s, timestamp=%s, read=%s WHERE id=%s"
    cursor.execute(sql, (notification.user_id, notification.message, notification.timestamp, notification.read, id))
    db.commit()
    db.close()
    return {"message": "Notification updated successfully"}

@app.delete("/notifications/{id}")
def delete_notification(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("DELETE FROM Notifications WHERE id=%s", (id,))
    db.commit()
    db.close()
    return {"message": "Notification deleted successfully"}

@app.get("/supporttickets")
def get_supporttickets():
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM SupportTickets")
    result = cursor.fetchall()
    db.close()
    return {"supporttickets": result}

@app.post("/supporttickets")
def add_supportticket(supportticket: schemas.SupportTicket):
    db = get_db()
    cursor = db.cursor()
    sql = "INSERT INTO SupportTickets (user_id, subject, description, status, created_at) VALUES (%s, %s, %s, %s, %s)"
    cursor.execute(sql, (supportticket.user_id, supportticket.subject, supportticket.description, supportticket.status, supportticket.created_at))
    db.commit()
    db.close()
    return {"message": "Support ticket created successfully"}

@app.get("/supporttickets/{id}")
def get_supportticket(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT * FROM SupportTickets WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    return {"supportticket": result}

@app.put("/supporttickets/{id}")
def update_supportticket(id: int, supportticket: schemas.SupportTicket):
    db = get_db()
    cursor = db.cursor()
    sql = "UPDATE SupportTickets SET user_id=%s, subject=%s, description=%s, status=%s, created_at=%s WHERE id=%s"
    cursor.execute(sql, (supportticket.user_id, supportticket.subject, supportticket.description, supportticket.status, supportticket.created_at, id))
    db.commit()
    db.close()
    return {"message": "Support ticket updated successfully"}

@app.delete("/supporttickets/{id}")
def delete_supportticket(id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("DELETE FROM SupportTickets WHERE id=%s", (id,))
    db.commit()
    db.close()
    return {"message": "Support ticket deleted successfully"}

