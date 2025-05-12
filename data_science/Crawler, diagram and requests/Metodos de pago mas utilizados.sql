SELECT 
    metodo,
    COUNT(DISTINCT orden_id) AS total_transacciones,
    ROUND(SUM(monto), 2) AS monto_total
FROM 
    ecommerce_catalogo.crawled_ingesta_ecommerce
WHERE 
    metodo IS NOT NULL
GROUP BY 
    metodo
ORDER BY 
    total_transacciones DESC;