CREATE VIEW vista_usuarios_total_gasto AS
SELECT 
  user_id,
  firstname,
  lastname,
  SUM(monto) AS total_gastado
FROM 
  ecommerce_ingesta_ecommerce
WHERE 
  monto IS NOT NULL 
  AND user_id IS NOT NULL
GROUP BY 
  user_id, firstname, lastname;