// routes/healthcheck.ts
import { Router } from "../deps.ts";

const router = new Router();

// Health check en ruta raíz
router.get("/", (ctx) => {
  ctx.response.body = "echo test ok";
  ctx.response.status = 200;
});

// Opcional: endpoint adicional de health check
router.get("/health", (ctx) => {
  ctx.response.body = { status: "healthy" };
  ctx.response.status = 200;
});

export default router;