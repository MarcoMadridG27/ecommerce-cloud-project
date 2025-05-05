import { MongoClient } from "../deps.ts";

// Cambia "localhost" por el nombre del servicio de MongoDB en Docker
const client = new MongoClient();
await client.connect("mongodb://db_ordenes_pagos:27017");  // Cambiado a db_ordenes_pagos

const db = client.database("ordenes_pagos");

export const ordenes = db.collection("ordenes");
export const pagos = db.collection("pagos");
