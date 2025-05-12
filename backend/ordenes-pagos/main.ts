import { Application } from "./deps.ts";
import ordenesRouter from "./routes/ordenes.ts";
import pagosRouter from "./routes/pagos.ts";
import healthcheckRouter from "./routes/healthcheck.ts";  // Nuevo import

const app = new Application();

app.use(async (ctx, next) => {
    ctx.response.headers.set("Access-Control-Allow-Origin", "*");
    ctx.response.headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    ctx.response.headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    
    if (ctx.request.method === "OPTIONS") {
      ctx.response.status = 204;
      return;
    }
    
    await next();
  });

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

console.log("🚀 Servidor corriendo en http://0.0.0.0:8000");
await app.listen({ hostname: "0.0.0.0", port: 8000 });

