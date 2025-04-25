import { Router } from "../deps.ts";
import { pagos } from "../db/mongo.ts";

const router = new Router();

router
  .get("/pagos", async (ctx) => {
    const data = await pagos.find();
    ctx.response.body = data;
  })
  .post("/pagos", async (ctx) => {
    const body = await ctx.request.body().value;
    body.fecha = new Date().toISOString();
    const id = await pagos.insertOne(body);
    ctx.response.status = 201;
    ctx.response.body = { insertedId: id };
  })
  .get("/pagos/orden/:orden_id", async (ctx) => {
    const orden_id = ctx.params.orden_id!;
    const pagosOrden = await pagos.find({ orden_id });
    ctx.response.body = pagosOrden;
  });

export default router;
