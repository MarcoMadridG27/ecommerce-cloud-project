import { faker } from "https://deno.land/x/deno_faker@v1.0.3/mod.ts";
import { MongoClient } from "https://deno.land/x/mongo@v0.31.1/mod.ts";

const client = new MongoClient();
await client.connect("mongodb://localhost:27017");
const db = client.database("ecommerce");
const ordenes = db.collection("ordenes");
const pagos = db.collection("pagos");

const estadosOrden = ["pendiente", "enviado", "entregado"];
const metodosPago = ["tarjeta", "transferencia", "efectivo"];
const estadosPago = ["pendiente", "completado", "fallido"];

// Función para generar una orden
function generarOrden() {
  return {
    usuario_id: crypto.randomUUID(),
    productos: Array.from({ length: Math.floor(Math.random() * 5) + 1 }, () => ({
      producto_id: crypto.randomUUID(),
      cantidad: Math.floor(Math.random() * 3) + 1,
    })),
    total: parseFloat(faker.commerce.price(20, 500)),
    fecha: faker.date.past().toISOString(),
    estado: estadosOrden[Math.floor(Math.random() * estadosOrden.length)],
  };
}

// Función para generar un pago
function generarPago(orden_id: string) {
  return {
    orden_id,
    metodo: metodosPago[Math.floor(Math.random() * metodosPago.length)],
    monto: parseFloat(faker.commerce.price(20, 500)),
    fecha: new Date().toISOString(),
    estado: estadosPago[Math.floor(Math.random() * estadosPago.length)],
  };
}

// Generación masiva
console.log("Generando 20,000 órdenes y pagos...");
for (let i = 0; i < 20000; i++) {
  const orden = generarOrden();
  const inserted = await ordenes.insertOne(orden);
  const pago = generarPago(inserted.$oid);
  await pagos.insertOne(pago);

  if (i % 1000 === 0) console.log(`Insertados ${i}...`);
}
console.log("✅ Fake data generada correctamente.");
