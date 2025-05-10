import pandas as pd
import psycopg2
import boto3
import os

conn = psycopg2.connect(
    host='172.31.28.116',
    port=5432,
    database='productos_db',
    user='user',
    password='password'
)

print("✅ Conectado a PostgreSQL")

tablas = ['categories', 'products', 'inventory']
bucket_name = 'ingesta-ecommerce'

s3 = boto3.client('s3')

for tabla in tablas:
    df = pd.read_sql(f"SELECT * FROM {tabla}", conn)
    filename = f"/tmp/{tabla}.csv"
    df.to_csv(filename, index=False)
    print(f"📁 Archivo generado: {filename}")
    s3.upload_file(filename, bucket_name, f"{tabla}.csv")
    print(f"✅ Subido a S3: s3://{bucket_name}/{tabla}.csv")
    os.remove(filename)

conn.close()