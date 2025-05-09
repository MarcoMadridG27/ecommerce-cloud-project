# 🛒 Ecommerce Cloud Project

Este proyecto es una arquitectura basada en microservicios para la gestión de un sistema de comercio electrónico, usando diferentes tecnologías en contenedores Docker.

---

## 📦 Estructura del Proyecto

```bash
ecommerce-cloud-project/
├── backend/
│   ├── productos-inventario/
│   │   ├── Dockerfile
│   │   ├── src/
│   │   ├── data/
│   │   ├── docs/
│   │   │   ├── index.html
│   │   │   ├── api_productos_inventario.yaml
│   │   │   └── swagger-ui assets...
│   ├── usuarios-direcciones/
│   ├── ordenes-pagos/
│   ├── orquestador/
│   ├── docker-compose.yml
├── catalog/
│   └── api_productos_inventario.yaml (migrado al contenedor)
