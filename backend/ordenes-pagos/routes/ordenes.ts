import { Router } from "../deps.ts";
import { ordenes } from "../db/mongo.ts";

const ordenesRouter = new Router();

ordenesRouter
  .get("/ordenes", async (ctx) => {
    try {
      const url = ctx.request.url;
      const limit = parseInt(url.searchParams.get("limit") || "10");
      const page = parseInt(url.searchParams.get("page") || "1");

      const skip = (page - 1) * limit;

      const total = await ordenes.countDocuments();
      const data = await ordenes.find({}, { skip, limit }).toArray();

      ctx.response.headers.set("Content-Type", "application/json");  // ✅
      ctx.response.body = {
        page,
        limit,
        total,
        totalPages: Math.ceil(total / limit),
        data,
      };

      console.log(`📦 Página ${page} devuelta con ${data.length} orden(es)`);
    } catch (err) {
      console.error("❌ Error en paginación:", err.message);
      ctx.response.status = 500;
      ctx.response.body = { error: "Error al obtener las órdenes con paginación" };
    }
  })

  .get("/ordenes/:id", async (ctx) => {
    try {
      const id = ctx.params.id!;
      const orden = await ordenes.findOne({ _id: id }); // mantén este si usas UUID en lugar de ObjectId

      if (orden) {
        ctx.response.headers.set("Content-Type", "application/json");  // ✅
        ctx.response.body = orden;
      } else {
        ctx.response.status = 404;
        ctx.response.body = { error: "Orden no encontrada" };
      }
    } catch (error) {
      console.error("❌ Error al buscar orden:", error);
      ctx.response.status = 400;
      ctx.response.body = { error: "ID inválido" };
    }
  })

  .post("/ordenes", async (ctx) => {
    const body = await ctx.request.body().value;
    const usuario_id = body.usuario_id;

    const usuariosHost = Deno.env.get("USUARIOS_DIRECCIONES_HOST") || "http://localhost:8001";
    const response = await fetch(`${usuariosHost}/users/${usuario_id}`);
    
    if (!response.ok) {
      ctx.response.status = 400;
      ctx.response.body = { error: "Usuario no válido o no encontrado" };
      return;
    }

    body.fecha = new Date().toISOString();
    const id = await ordenes.insertOne(body);
    ctx.response.status = 201;
    ctx.response.headers.set("Content-Type", "application/json");  // ✅
    ctx.response.body = { insertedId: id };
  });

export default ordenesRouter;
