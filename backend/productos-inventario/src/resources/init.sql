CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    category_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    category_id INT REFERENCES categories(id),
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    product_id INT PRIMARY KEY REFERENCES products(id),
    quantity INT NOT NULL
);
