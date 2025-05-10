import mysql.connector
import pandas as pd
import boto3
import os

config = {
    'host': '172.31.28.116',
    'port': 3306,
    'user': 'root',
    'password': 'utec',
    'database': 'usuarios_db'
}

try:
    conn = mysql.connector.connect(**config)
    print("✅ Conectado a MySQL")
except mysql.connector.Error as err:
    print(f"❌ Error de conexión: {err}")
    exit()

query = "SELECT * FROM Users"
df = pd.read_sql(query, conn)
file_path = "/tmp/usuarios_datos.csv"
df.to_csv(file_path, index=False)

s3 = boto3.client("s3")
bucket_name = "ingesta-ecommerce"
s3.upload_file(file_path, bucket_name, "usuarios_datos.csv")
print(f"✅ Archivo subido a s3://{bucket_name}/usuarios_datos.csv")
os.remove(file_path)
conn.close()