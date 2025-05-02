FROM denoland/deno:2.3.1

EXPOSE 8000
WORKDIR /app

# Copiar archivos y preparar dependencias
COPY deps.ts .
RUN deno cache deps.ts

COPY . .

# Cachear los scripts importantes
RUN deno cache main.ts scripts/generar_datos.ts
CMD ["sh", "-c", "deno run --allow-net --allow-read --unstable scripts/generar_datos.ts && deno run --allow-net main.ts"]
