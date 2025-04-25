import { Application } from "./deps.ts";
import ordenesRouter from "./routes/ordenes.ts";
import pagosRouter from "./routes/pagos.ts";

const app = new Application();

app.use(ordenesRouter.routes());
app.use(ordenesRouter.allowedMethods());

app.use(pagosRouter.routes());
app.use(pagosRouter.allowedMethods());

console.log("🚀 Servidor corriendo en http://localhost:8000");
await app.listen({ port: 8000 });
