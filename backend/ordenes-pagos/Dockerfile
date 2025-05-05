FROM denoland/deno:2.3.1

EXPOSE 8000
WORKDIR /app

COPY . .

RUN deno cache deps.ts
RUN deno cache main.ts scripts/generar_datos.ts

CMD ["sh", "-c", "deno run --allow-net --allow-read scripts/generar_datos.ts && deno run --allow-net main.ts"]
