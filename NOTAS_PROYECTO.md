# FinTrack Pro — Bloc de Notas Maestro y Guía de Apertura

## Cómo abrir cada parte del sistema (Servidor en http://localhost:8080)
1. **Portal Principal**: `http://localhost:8080/` (`index.html`) — Vista general de accesos y estado de tokens.
2. **Dashboard Admin**: `http://localhost:8080/admin.html` (`admin.html`) — Crear departamentos, asignar presupuestos mensuales, ver KPIs, barras de uso y alertas `>=80%` o sobregiro.
3. **Dashboard Financiero**: `http://localhost:8080/financiero.html` (`financiero.html`) — Registrar ingresos/egresos, filtrar movimientos, ver balance y consultar al Agente Financiero con Tool Calling.
4. **Dashboard Empleado**: `http://localhost:8080/empleado.html` (`empleado.html`) — Registrar gastos operativos del área, ver presupuesto/saldo del departamento y revisar gastos recientes.
5. **Bloc de Notas Web**: `http://localhost:8080/notas.html` (`notas.html`) — Guía de apertura, arquitectura, endpoints y notas editables.

## Endpoints REST principales
- `POST /api/auth/login` -> Genera JWT HS256 (`{"username": "...", "role": "ADMIN|FINANCIERO|EMPLEADO"}`).
- `GET /api/departamentos` -> Lista departamentos con `presupuestoMensual`, `gastoAcumulado`, `saldoDisponible`, `porcentajeUsado` y `alerta`.
- `POST /api/departamentos` -> Crea departamento (requiere `ROLE_ADMIN`).
- `GET /api/departamentos/{id}/presupuesto` -> Consulta estado presupuestario de un departamento.
- `GET /api/transacciones` y `POST /api/transacciones` -> Lista y registra `INGRESO` o `EGRESO` (`monto > 0`).
- `POST /api/agente/consultar` -> Ejecuta el agente con Tool Calling (`consultar_presupuesto_departamento` y `convertir_pen_a_usd`).

## Arquitectura Hexagonal (`com.fintrack.pro`)
- **Núcleo puro (`domain` + `application`)**: `Transaccion`, `Departamento`, `PresupuestoEstado`, puertos `in`/`out`, `RegistrarTransaccionService`, `ConsultarPresupuestoService` (sin Spring ni JPA).
- **Infraestructura (`infrastructure`)**: `BeanConfig`, adaptadores JPA (`springdata`), `TipoCambioHttpAdapter`, seguridad JWT (`JwtUtil`, `JwtFilter`, `SecurityConfig`), agente (`Herramienta`, `PresupuestoDepartamentoTool`, `ConvertirMonedaTool`, `ClienteLlm`, `AgenteFinanciero`) y controladores REST.