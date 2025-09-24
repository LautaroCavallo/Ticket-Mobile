-- Queries H1 - Sistema de Gestión de Tickets

-- 1. Contar tickets por estado
SELECT status, COUNT(*) AS cantidad
FROM tickets
GROUP BY status;

-- 2. Contar tickets por prioridad
SELECT priority, COUNT(*) AS cantidad
FROM tickets
GROUP BY priority;

-- 3. Contar tickets por categoría
SELECT c.nombre AS categoria, COUNT(t.id) AS cantidad
FROM tickets t
JOIN categorias c ON t.categoriaId = c.id
GROUP BY c.nombre;

-- 4. Tickets creados por usuario
SELECT u.firstName || ' ' || u.lastName AS usuario, COUNT(t.id) AS cantidad
FROM tickets t
JOIN users u ON t.creatorId = u.id
GROUP BY usuario;

-- 5. Tiempo promedio de resolución (en horas)
SELECT AVG(EXTRACT(EPOCH FROM (resolvedAt - createdAt)) / 3600) AS avg_resolution_hours
FROM tickets
WHERE resolvedAt IS NOT NULL;

-- 6. Tickets resueltos hoy
SELECT COUNT(*) AS tickets_resueltos_hoy
FROM tickets
WHERE DATE(resolvedAt) = CURRENT_DATE;

-- 7. Tickets creados hoy
SELECT COUNT(*) AS tickets_creados_hoy
FROM tickets
WHERE DATE(createdAt) = CURRENT_DATE;
