// scripts/init_db.ts
import { faker } from "https://deno.land/x/deno_faker@v1.0.3/mod.ts";
import { MongoClient } from "https://deno.land/x/mongo@v0.31.1/mod.ts";

const client = new MongoClient();
await client.connect("mongodb://db_ordenes_pagos:27017");
const db = client.database("ordenes_pagos");
const ordenes = db.collection("ordenes");
const pagos = db.collection("pagos");

const estadosOrden = ["pendiente", "enviado", "entregado"];
const metodosPago = ["tarjeta", "transferencia", "efectivo"];
const estadosPago = ["pendiente", "completado", "fallido"];

const ordenesCount = await ordenes.countDocuments();
if (ordenesCount === 0) {
  try {
    const ordenesData = JSON.parse(await Deno.readTextFile("./data/ordenes.json"));
    const pagosData = JSON.parse(await Deno.readTextFile("./data/pagos.json"));

    await ordenes.insertMany(ordenesData);
    await pagos.insertMany(pagosData);

    console.log("✅ Datos cargados desde archivos JSON.");
  } catch (error) {
    console.log("⚠️ Archivos no encontrados o error al leerlos. Generando datos falsos...");

    for (let i = 0; i < 20000; i++) {
      const orden = {
        usuario_id: crypto.randomUUID(),
        productos: Array.from({ length: Math.floor(Math.random() * 5) + 1 }, () => ({
          producto_id: crypto.randomUUID(),
          cantidad: Math.floor(Math.random() * 3) + 1,
        })),
        total: parseFloat(faker.commerce.price(20, 500)),
        fecha: faker.date.past().toISOString(),
        estado: estadosOrden[Math.floor(Math.random() * estadosOrden.length)],
      };

      const inserted = await ordenes.insertOne(orden);
      const pago = {
        orden_id: inserted.$oid,
        metodo: metodosPago[Math.floor(Math.random() * metodosPago.length)],
        monto: orden.total,
        fecha: new Date().toISOString(),
        estado: estadosPago[Math.floor(Math.random() * estadosPago.length)],
      };
      await pagos.insertOne(pago);

      if (i % 1000 === 0) console.log(`Insertados ${i}...`);
    }

    console.log("✅ Datos falsos generados correctamente.");
  }
} else {
  console.log("ℹ️ La base de datos ya contiene datos. No se realizó ninguna carga.");
}
