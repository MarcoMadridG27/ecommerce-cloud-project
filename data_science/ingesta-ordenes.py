import pandas as pd
import pymongo
import boto3
import os

client = pymongo.MongoClient("mongodb://172.31.28.116:27017/")
db = client["ordenes_pagos"]
bucket_name = 'ingesta-ecommerce'

colecciones = ['ordenes', 'pagos']
s3 = boto3.client('s3')

for col in colecciones:
    data = list(db[col].find())
    if not data:
        print(f"⚠️ Colección vacía: {col}")
        continue

    df = pd.DataFrame(data)
    if '_id' in df.columns:
        df['_id'] = df['_id'].astype(str)

    filename = f"/tmp/{col}.csv"
    df.to_csv(filename, index=False)
    print(f"📁 Archivo generado: {filename}")
    s3.upload_file(filename, bucket_name, f"{col}.csv")
    print(f"✅ Subido a S3: s3://{bucket_name}/{col}.csv")
    os.remove(filename)