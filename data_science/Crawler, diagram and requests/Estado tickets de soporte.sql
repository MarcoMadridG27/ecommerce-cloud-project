SELECT 
    ticket_status,
    COUNT(*) AS cantidad_tickets,
    ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (), 2) AS porcentaje
FROM 
    ecommerce_catalogo.crawled_ingesta_ecommerce
WHERE 
    ticket_status IS NOT NULL
GROUP BY 
    ticket_status
ORDER BY 
    cantidad_tickets DESC;