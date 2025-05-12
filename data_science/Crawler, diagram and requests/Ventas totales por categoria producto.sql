SELECT 
    category_name,
    COUNT(DISTINCT orden_id) AS total_ventas,
    ROUND(SUM(total), 2) AS ingresos_totales
FROM 
    ecommerce_catalogo.crawled_ingesta_ecommerce
WHERE 
    category_name IS NOT NULL
    AND orden_id IS NOT NULL
GROUP BY 
    category_name
ORDER BY 
    ingresos_totales DESC
LIMIT 10;