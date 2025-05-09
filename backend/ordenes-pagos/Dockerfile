FROM denoland/deno:2.3.1

EXPOSE 8000
WORKDIR /app

COPY . .

RUN deno cache deps.ts \
    && deno cache main.ts scripts/generar_datos.ts

CMD deno run --allow-read --allow-net --allow-env scripts/generar_datos.ts || true \
    && deno run --allow-net --allow-env main.ts
