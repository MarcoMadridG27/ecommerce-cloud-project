CREATE VIEW vista_inventario_bajo AS
SELECT 
    product_id,
    name AS producto,
    quantity AS inventario,
    price AS precio
FROM 
    ecommerce_catalogo.crawled_ingesta_ecommerce
WHERE 
    product_id IS NOT NULL
    AND quantity < 100 
    AND name IS NOT NULL
ORDER BY 
    quantity ASC;