import { Application } from "./deps.ts";
import ordenesRouter from "./routes/ordenes.ts";
import pagosRouter from "./routes/pagos.ts";
import healthcheckRouter from "./routes/healthcheck.ts";  // Nuevo import

const app = new Application();


app.use(async (ctx, next) => {
  const start = Date.now();
  await next();
  const ms = Date.now() - start;
  console.log(`${ctx.request.method} ${ctx.request.url.pathname} - ${ms}ms`);
});

app.use(healthcheckRouter.routes());
app.use(healthcheckRouter.allowedMethods());

app.use(ordenesRouter.routes());
app.use(ordenesRouter.allowedMethods());

app.use(pagosRouter.routes());
app.use(pagosRouter.allowedMethods());

console.log("🚀 Servidor corriendo en http://localhost:8000");
await app.listen({ port: 8000 });