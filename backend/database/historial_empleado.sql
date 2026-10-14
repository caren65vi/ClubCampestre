-- Actualizar empleado: estado "vacaciones" y bitácora de cambios (historial_empleado)
-- Ejecutar sobre la base villa_Katy. Se puede correr más de una vez.

IF EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'ck_empleado_estado')
    ALTER TABLE empleado DROP CONSTRAINT ck_empleado_estado;
GO

ALTER TABLE empleado ADD CONSTRAINT ck_empleado_estado
    CHECK (estado IN ('activo', 'vacaciones', 'inactivo'));
GO

IF OBJECT_ID('historial_empleado', 'U') IS NULL
BEGIN
    CREATE TABLE historial_empleado (
        id_historial              INT IDENTITY(1,1) PRIMARY KEY,
        numero_documento_empleado VARCHAR(20)  NOT NULL,
        tipo_evento               VARCHAR(10)  NOT NULL,
        estado_anterior           VARCHAR(10)  NULL,
        estado_nuevo              VARCHAR(10)  NOT NULL,
        fecha_efectiva            DATE         NOT NULL,
        motivo                    VARCHAR(300) NOT NULL,
        usuario_responsable       VARCHAR(50)  NULL,
        fecha_registro            DATETIME2    NOT NULL CONSTRAINT df_historial_empleado_fecha DEFAULT SYSDATETIME(),
        CONSTRAINT fk_historial_empleado_empleado FOREIGN KEY (numero_documento_empleado)
            REFERENCES empleado (numero_documento),
        CONSTRAINT ck_historial_empleado_tipo CHECK (tipo_evento IN ('estado', 'edicion'))
    );

    CREATE INDEX ix_historial_empleado_empleado
        ON historial_empleado (numero_documento_empleado, fecha_registro DESC);
END
GO
