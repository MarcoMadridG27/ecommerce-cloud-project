import { Router } from "../deps.ts";
import { ordenes } from "../db/mongo.ts";

const router = new Router();

router
  .get("/ordenes", async (ctx) => {
    const data = await ordenes.find();
    ctx.response.body = data;
  })
  .get("/ordenes/:id", async (ctx) => {
    const id = ctx.params.id!;
    const orden = await ordenes.findOne({ _id: { $oid: id } });
    ctx.response.body = orden ?? { error: "Orden no encontrada" };
  })
  .post("/ordenes", async (ctx) => {
    const body = await ctx.request.body().value;
    body.fecha = new Date().toISOString();
    const id = await ordenes.insertOne(body);
    ctx.response.status = 201;
    ctx.response.body = { insertedId: id };
  });

export default router;
