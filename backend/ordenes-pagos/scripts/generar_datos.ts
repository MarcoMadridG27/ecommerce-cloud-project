import { MongoClient } from "https://deno.land/x/mongo@v0.31.1/mod.ts";

const client = new MongoClient();
await client.connect("mongodb://172.31.28.116:27017");
const db = client.database("ordenes_pagos");
const ordenes = db.collection("ordenes");
const pagos = db.collection("pagos");

const ordenesCount = await ordenes.countDocuments();
if (ordenesCount === 0) {
  try {
    const ordenesDataRaw = JSON.parse(await Deno.readTextFile("./data/ordenes.json"));
    const pagosDataRaw = JSON.parse(await Deno.readTextFile("./data/pagos.json"));

    const ordenesData = ordenesDataRaw.map((o: any) => ({
      ...o,
      _id: o._id.toString(), 
    }));

    const pagosData = pagosDataRaw.map((p: any) => ({
      ...p,
      _id: p._id.toString(),
      orden_id: p.orden_id.toString(),
    }));

    await ordenes.insertMany(ordenesData);
    await pagos.insertMany(pagosData);

    console.log("✅ Datos cargados desde archivos JSON.");
  } catch (error) {
    console.error("❌ Error al leer los archivos JSON:", error.message);
    console.error("⚠️ Asegúrate de que existan los archivos ./data/ordenes.json y ./data/pagos.json");
  }
} else {
  console.log("ℹ️ La base de datos ya contiene datos. No se realizó ninguna carga.");
}
