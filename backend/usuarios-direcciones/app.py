from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import mysql.connector
import schemas
import pandas as pd
from passlib.hash import bcrypt
from email.message import EmailMessage
from jinja2 import Environment, FileSystemLoader
import smtplib
import os
from fastapi import HTTPException



app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Puedes restringir esto a tu frontend si deseas mayor seguridad
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

db_config = {
    "host": os.environ.get("DB_HOST", "localhost"),
    "port": int(os.environ.get("DB_PORT", 3306)),
    "user": os.environ.get("DB_USER", "root"),
    "password": os.environ.get("DB_PASSWORD", "utec"),
    "database": os.environ.get("DB_NAME", "usuarios_db")
}


def poblar_si_vacio():
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM Users")
    if cursor.fetchone()[0] == 0:
        print("📥 Poniendo datos desde CSV...")

        cargar_users(cursor)
        cargar_addresses(cursor)
        cargar_notifications(cursor)
        cargar_support_tickets(cursor)

        conn.commit()
    conn.close()
def cargar_users(cursor):
    df = pd.read_csv(
        "/programas/api-users/data/users.csv",
        header=None,
        names=["id", "firstname", "lastname", "phonenumber", "email", "age", "password"]
    )

    for _, row in df.iterrows():
        cursor.execute("""
            INSERT INTO Users (id, firstname, lastname, phonenumber, email, age, password)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, (
            int(row["id"]),
            row["firstname"],
            row["lastname"],
            row["phonenumber"],
            row["email"],
            int(row["age"]),
            row["password"]
        ))

def cargar_addresses(cursor):
    df = pd.read_csv("/programas/api-users/data/addresses.csv", header=None,
                     names=["user_id", "address_line", "city", "country"])
    for _, row in df.iterrows():
        cursor.execute("""
            INSERT INTO Addresses (user_id, address_line, city, country)
            VALUES (%s, %s, %s, %s)
        """, (int(row["user_id"]), row["address_line"], row["city"], row["country"]))

def cargar_notifications(cursor):
    df = pd.read_csv("/programas/api-users/data/notifications.csv", header=None,
                     names=["id", "user_id", "message", "timestamp", "read"])
    for _, row in df.iterrows():
        cursor.execute("""
            INSERT INTO Notifications (id, user_id, message, timestamp, `read`)
            VALUES (%s, %s, %s, %s, %s)
        """, (int(row["id"]), int(row["user_id"]), row["message"], row["timestamp"], bool(int(row["read"]))))

def cargar_support_tickets(cursor):
    df = pd.read_csv("/programas/api-users/data/supporttickets.csv", header=None,
                     names=["id", "user_id", "subject", "description", "status", "created_at"])
    for _, row in df.iterrows():
        cursor.execute("""
            INSERT INTO SupportTickets (id, user_id, subject, description, status, created_at)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (
            int(row["id"]), int(row["user_id"]), row["subject"], row["description"],
            row["status"], row["created_at"]
        ))
def get_db():
    return mysql.connector.connect(**db_config)
poblar_si_vacio()
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
    sql = "INSERT INTO Users (firstname, lastname, phonenumber, email, age,password) VALUES (%s, %s, %s, %s, %s,%s)"
    hashed_password = bcrypt.hash(user.password)
    cursor.execute(sql, (user.firstname, user.lastname, user.phonenumber, user.email, user.age,hashed_password))
    db.commit()
    db.close()
    return {"message": "User created successfully"}

@app.get("/users/{id}")
def get_user(id: int):
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT id, firstname, lastname, phonenumber, email, age FROM Users WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()

    if result:
        return {
            "id": result["id"],
            "nombre": f"{result['firstname']} {result['lastname']}",
            "phonenumber": result["phonenumber"],
            "email": result["email"],
            "age": result["age"]
        }
    else:
        raise HTTPException(status_code=404, detail="User not found")



@app.put("/users/{id}")
def update_user(id: int, user: schemas.User):
    db = get_db()
    cursor = db.cursor()

    if user.password:
        hashed_password = bcrypt.hash(user.password)
        sql = """UPDATE Users 
                 SET firstname=%s, lastname=%s, phonenumber=%s, email=%s, age=%s, password=%s 
                 WHERE id=%s"""
        cursor.execute(sql, (
            user.firstname, user.lastname, user.phonenumber, user.email, user.age,
            hashed_password, id
        ))
    else:

        sql = """UPDATE Users 
                 SET firstname=%s, lastname=%s, phonenumber=%s, email=%s, age=%s 
                 WHERE id=%s"""
        cursor.execute(sql, (
            user.firstname, user.lastname, user.phonenumber, user.email, user.age, id
        ))

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
def add_address(address: schemas.Address):
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM Addresses WHERE user_id = %s", (address.user_id,))
    if cursor.fetchone():
        db.close()
        raise HTTPException(status_code=409, detail="User already has an address")

    cursor = db.cursor()
    sql = "INSERT INTO Addresses (user_id, address_line, city, country) VALUES (%s, %s, %s, %s)"
    cursor.execute(sql, (address.user_id, address.address_line, address.city, address.country))
    db.commit()
    db.close()
    return {"message": "Address created successfully"}

@app.get("/addresses/user/{user_id}")
def get_address_by_user_id(user_id: int):
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM Addresses WHERE user_id = %s", (user_id,))
    result = cursor.fetchone()
    db.close()

    if result:
        return {"address": result}
    else:
        return {"error": "Address not found for user"}


@app.put("/addresses/user/{user_id}")
def update_address(user_id: int, address: schemas.Address):
    db = get_db()
    cursor = db.cursor()
    sql = "UPDATE Addresses SET address_line=%s, city=%s, country=%s WHERE user_id=%s"
    cursor.execute(sql, (address.address_line, address.city, address.country, user_id))
    db.commit()
    db.close()
    return {"message": "Address updated successfully"}

@app.delete("/addresses/user/{user_id}")
def delete_address(user_id: int):
    db = get_db()
    cursor = db.cursor()
    cursor.execute("DELETE FROM Addresses WHERE user_id=%s", (user_id,))
    db.commit()
    db.close()
    return {"message": "Address deleted successfully"}

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
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM Notifications WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    if result:
        return {"notification": result}
    else:
        return {"error": "Notification not found"}

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
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM SupportTickets WHERE id = %s", (id,))
    result = cursor.fetchone()
    db.close()
    if result:
        return {"supportticket": result}
    else:
        return {"error": "Support ticket not found"}

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

@app.post("/login")
def login(data: schemas.LoginData):
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM Users WHERE email = %s", (data.email,))
    user = cursor.fetchone()
    db.close()

    if user and bcrypt.verify(data.password, user["password"]):
        return {"message": "Login successful", "user_id": user["id"], "name": user["firstname"]}
    raise HTTPException(status_code=401, detail="Invalid email or password")

def enviar_correo_confirmacion_html(destinatario: str, nombre: str):
    remitente = "marco.madrid@utec.edu.pe"
    clave = "kmxw tfti pvvs pzlq"

    env = Environment(loader=FileSystemLoader("templates"))
    template = env.get_template("welcome_email.html")
    html_content = template.render(nombre=nombre)

    mensaje = EmailMessage()
    mensaje["Subject"] = "Bienvenido a nuestra tienda"
    mensaje["From"] = remitente
    mensaje["To"] = destinatario
    mensaje.set_content("Este mensaje requiere un cliente compatible con HTML.")
    mensaje.add_alternative(html_content, subtype="html")

    try:
        with smtplib.SMTP_SSL("smtp.gmail.com", 465) as smtp:
            smtp.login(remitente, clave)
            smtp.send_message(mensaje)
            print(f"✅ Correo HTML enviado a {destinatario}")
    except Exception as e:
        print("❌ Error al enviar correo:", e)

@app.post("/register")
def register(user: schemas.User):
    db = get_db()
    cursor = db.cursor()
    hashed_pw = bcrypt.hash(user.password)
    sql = "INSERT INTO Users (firstname, lastname, phonenumber, email, age, password) VALUES (%s, %s, %s, %s, %s, %s)"
    cursor.execute(sql, (user.firstname, user.lastname, user.phonenumber, user.email, user.age, hashed_pw))
    db.commit()
    db.close()

    enviar_correo_confirmacion_html(user.email, user.firstname) 
    return {"message": "User registered successfully. Confirmation email sent."}
