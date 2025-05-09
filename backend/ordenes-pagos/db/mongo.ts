import { MongoClient } from "../deps.ts";

const mongoUrl = Deno.env.get("MONGO_DB_URL");

const client = new MongoClient();
await client.connect(mongoUrl);

const db = client.database("ordenes_pagos");

export const ordenes = db.collection("ordenes");
export const pagos = db.collection("pagos");
