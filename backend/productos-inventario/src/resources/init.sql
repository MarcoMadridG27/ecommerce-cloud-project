-- Crear categorías
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    category_name VARCHAR(100)
);

-- Crear proveedores
CREATE TABLE IF NOT EXISTS suppliers (
    id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(100)
);

-- Crear productos
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    category_id INT REFERENCES categories(id),
    supplier_id INT REFERENCES suppliers(id)
);

-- Crear inventario
CREATE TABLE IF NOT EXISTS inventory (
    id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products(id),
    quantity INT
);

