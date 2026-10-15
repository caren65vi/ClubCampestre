-- Datos de prueba para las evidencias GA7-220501096 (AA2 a AA5) - Club Campestre Villa Katy
-- Agrega las áreas funcionales del requisito RF-0110, un cargo por área y un usuario
-- administrador de pruebas. Se puede ejecutar más de una vez: solo inserta lo que falta.
-- La contraseña del usuario de pruebas NO está en este archivo: se guarda en el .env local
-- (PRUEBAS_CONTRASENA). Aquí solo va su hash BCrypt.

DECLARE @areas TABLE (nombre VARCHAR(50), descripcion VARCHAR(200), cargo VARCHAR(50), salario DECIMAL(10,2), nivel VARCHAR(50));
INSERT INTO @areas VALUES
 (N'Recepción',     N'Atención de huéspedes, reservas y registro de hospedaje', N'Recepcionista',          1750905.00, N'Operativo'),
 (N'Restaurante',   N'Servicio a la mesa del restaurante del club',            N'Mesero',                 1750905.00, N'Operativo'),
 (N'Cocina',        N'Preparación de alimentos',                               N'Cocinero',               1900000.00, N'Operativo'),
 (N'Bar',           N'Preparación y venta de bebidas',                         N'Bartender',              1800000.00, N'Operativo'),
 (N'Limpieza',      N'Aseo de habitaciones y zonas comunes',                   N'Auxiliar de limpieza',   1750905.00, N'Operativo'),
 (N'Mantenimiento', N'Mantenimiento de instalaciones, piscina y jacuzzi',      N'Técnico de mantenimiento', 2000000.00, N'Operativo');

INSERT INTO area_trabajo (nombre_area, descripcion_area, id_lider)
SELECT a.nombre, a.descripcion, NULL FROM @areas a
 WHERE NOT EXISTS (SELECT 1 FROM area_trabajo t WHERE t.nombre_area = a.nombre);

INSERT INTO cargo (nombre_cargo, descripcion_cargo, salario_base, nivel_jerarquico, id_area)
SELECT a.cargo, N'Cargo del área de ' + a.nombre, a.salario, a.nivel, t.id_area
  FROM @areas a JOIN area_trabajo t ON t.nombre_area = a.nombre
 WHERE NOT EXISTS (SELECT 1 FROM cargo c WHERE c.nombre_cargo = a.cargo);

-- Usuario administrador de pruebas (asociado a la empleada activa 1000000001)
IF NOT EXISTS (SELECT 1 FROM usuario WHERE nombre_usuario = 'admin.pruebas')
    INSERT INTO usuario (nombre_usuario, contrasena_hash, id_rol, numero_documento_empleado)
    VALUES ('admin.pruebas', '$2y$10$g4QMVUmOa8GDMoUKbR.ckegBJP8fEs7NflKPvknQa5h7WTX/GEWyG', 1, '1000000001');
