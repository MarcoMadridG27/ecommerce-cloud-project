from faker import Faker
import mysql.connector
import random
from datetime import datetime

fake = Faker()
db = mysql.connector.connect(
    host="db_usuarios",  
    user="root",
    password="utec",
    database="usuarios_db",
    port=3306 
)

cursor = db.cursor()

def crear_usuarios(n):
    for _ in range(n):
        sql = "INSERT INTO Users (firstname, lastname, phonenumber, email, age) VALUES (%s, %s, %s, %s, %s)"
        val = (
            fake.first_name(),
            fake.last_name(),
            fake.phone_number()[:20],
            fake.unique.email(),
            random.randint(18, 60)
        )
        cursor.execute(sql, val)
    db.commit()

def crear_addresses(n):
    for _ in range(n):
        sql = "INSERT INTO Addresses (user_id, address_line, city, country) VALUES (%s, %s, %s, %s)"
        val = (
            random.randint(1, 20000),  
            fake.street_address(),
            fake.city(),
            fake.country()
        )
        cursor.execute(sql, val)
    db.commit()

def crear_notifications(n):
    for _ in range(n):
        sql = "INSERT INTO Notifications (user_id, message, timestamp, `read`) VALUES (%s, %s, %s, %s)" 
        val = (
            random.randint(1, 20000),
            fake.sentence(),
            datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            random.choice([True, False])
        )
        cursor.execute(sql, val)
    db.commit()

def crear_tickets(n):
    for _ in range(n):
        sql = "INSERT INTO SupportTickets (user_id, subject, description, status, created_at) VALUES (%s, %s, %s, %s, %s)"
        val = (
            random.randint(1, 20000),
            fake.sentence(nb_words=4),
            fake.paragraph(),
            random.choice(['open', 'in_progress', 'resolved']),
            datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        )
        cursor.execute(sql, val)
    db.commit()

print("📦 Generando datos...")
crear_usuarios(20000)
crear_addresses(20000)
crear_notifications(20000)
crear_tickets(20000)
print("✅ Datos generados.")
