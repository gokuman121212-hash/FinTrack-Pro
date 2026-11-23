CREATE TABLE IF NOT EXISTS departamentos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    presupuesto_mensual NUMERIC(14, 2) NOT NULL CHECK (presupuesto_mensual >= 0),
    gasto_acumulado NUMERIC(14, 2) NOT NULL DEFAULT 0.00 CHECK (gasto_acumulado >= 0),
    permitir_sobregiro BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transacciones (
    id BIGSERIAL PRIMARY KEY,
    departamento_id BIGINT NOT NULL,
    tipo VARCHAR(16) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    monto NUMERIC(14, 2) NOT NULL CHECK (monto > 0),
    categoria VARCHAR(80) NOT NULL DEFAULT 'General',
    descripcion VARCHAR(255),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaccion_departamento FOREIGN KEY (departamento_id)
        REFERENCES departamentos (id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_transacciones_depto ON transacciones (departamento_id);
CREATE INDEX IF NOT EXISTS idx_transacciones_tipo ON transacciones (tipo);

CREATE TABLE IF NOT EXISTS usuarios_sistema (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'FINANCIERO', 'EMPLEADO')),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS auditoria_agente (
    id BIGSERIAL PRIMARY KEY,
    pregunta TEXT NOT NULL,
    respuesta TEXT NOT NULL,
    pasos_usados INT NOT NULL DEFAULT 1,
    latencia_ms BIGINT NOT NULL DEFAULT 0,
    consultado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);