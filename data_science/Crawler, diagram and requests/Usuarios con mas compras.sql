SELECT 
    user_id,
    firstname,
    lastname,
    city,
    COUNT(DISTINCT orden_id) AS total_compras,
    ROUND(SUM(total), 2) AS gasto_total
FROM 
    ecommerce_catalogo.crawled_ingesta_ecommerce
WHERE 
    user_id IS NOT NULL
    AND orden_id IS NOT NULL
GROUP BY 
    user_id, firstname, lastname, city
ORDER BY 
    gasto_total DESC
LIMIT 15;