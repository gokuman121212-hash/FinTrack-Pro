INSERT INTO usuarios_sistema (username, password_hash, rol, activo)
SELECT 'admin@fintrack.pro', 'Admin2026#', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios_sistema WHERE username = 'admin@fintrack.pro');

INSERT INTO usuarios_sistema (username, password_hash, rol, activo)
SELECT 'finanzas@fintrack.pro', 'Finanzas2026#', 'FINANCIERO', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios_sistema WHERE username = 'finanzas@fintrack.pro');

INSERT INTO usuarios_sistema (username, password_hash, rol, activo)
SELECT 'empleado@fintrack.pro', 'Empleado2026#', 'EMPLEADO', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios_sistema WHERE username = 'empleado@fintrack.pro');