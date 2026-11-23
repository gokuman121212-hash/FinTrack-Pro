/* === FinTrack Pro Clean Functional Module (com.fintrack.pro) === */
(function () {
  if (window.__CAFQ_FT_FINAL_CLEAN__) return;
  window.__CAFQ_FT_FINAL_CLEAN__ = true;

  const style = document.createElement("style");
  style.textContent = `
    body {
      background: #070812 !important;
      color: #f3f4f6 !important;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .ft-main-wrap {
      max-width: 1220px;
      margin: 18px auto;
      padding: 16px;
      border-radius: 14px;
      background: #0d1021;
      border: 1px solid rgba(139, 92, 246, 0.28);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.45);
    }
    .ft-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 10px;
      padding-bottom: 12px;
      border-bottom: 1px solid rgba(148, 163, 184, 0.14);
    }
    .ft-layout {
      display: grid;
      grid-template-columns: 1.25fr 1fr;
      gap: 16px;
      margin-top: 14px;
    }
    @media (max-width: 900px) {
      .ft-layout { grid-template-columns: 1fr; }
    }
    .ft-card {
      background: #080a16;
      border: 1px solid rgba(139, 92, 246, 0.22);
      border-radius: 12px;
      padding: 14px;
    }
    .ft-form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
      margin-top: 10px;
    }
    @media (max-width: 600px) {
      .ft-form-grid { grid-template-columns: 1fr; }
    }
    .ft-input {
      width: 100%;
      box-sizing: border-box;
      background: #0d1021;
      color: #f9fafb;
      border: 1px solid rgba(139, 92, 246, 0.35);
      border-radius: 8px;
      padding: 9px 11px;
      font-size: 13px;
    }
    .ft-btn {
      background: linear-gradient(135deg, #7c3aed, #9333ea);
      color: #ffffff;
      border: none;
      border-radius: 8px;
      padding: 8px 14px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
    }
    .ft-btn-ghost {
      background: rgba(139, 92, 246, 0.14);
      color: #e9d5ff;
      border: 1px solid rgba(168, 85, 247, 0.35);
      border-radius: 8px;
      padding: 6px 11px;
      font-size: 12px;
      font-weight: 600;
      cursor: pointer;
    }
    .ft-table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 10px;
      font-size: 12.5px;
    }
    .ft-table th, .ft-table td {
      padding: 7px 8px;
      border-bottom: 1px solid rgba(148, 163, 184, 0.12);
      text-align: left;
    }
    .ft-empty {
      color: #9ca3af;
      font-size: 12.5px;
      padding: 10px 0;
    }
  `;
  document.head.appendChild(style);

  const STORAGE_KEY = "cafq_fintrack_real_v1";
  const state = JSON.parse(localStorage.getItem(STORAGE_KEY) || "null") || {
    ciclo: "2026-11",
    areas: [],
    movimientos: [],
    auditoria: []
  };

  function persist() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }

  function registrarAuditoria(accion, detalle) {
    state.auditoria.unshift({
      fecha: new Date().toLocaleTimeString(),
      accion,
      detalle
    });
    state.auditoria = state.auditoria.slice(0, 20);
    persist();
  }

  function obtenerTramo(monto) {
    if (monto < 50) return "DIMINUTO";
    if (monto < 250) return "MODERADO";
    if (monto < 1000) return "ALTO";
    return "CRITICO";
  }

  function recomendacionArea(area) {
    const pres = Number(area.presupuesto || 0);
    const gast = Number(area.gasto || 0);
    const pct = pres > 0 ? (gast * 100) / pres : 0;
    if (pct >= 100) {
      return `${area.nombre} (${pct.toFixed(1)}%): 100% alcanzado. ${area.excepcion100 ? "Excepcion activa." : "Bloqueado hasta aprobar excepcion."}`;
    }
    if (pct >= 80) {
      return `${area.nombre} (${pct.toFixed(1)}%): Alerta del 80%. Prioriza gastos indispensables.`;
    }
    if (pct >= 50) {
      return `${area.nombre} (${pct.toFixed(1)}%): Ejecucion intermedia (50%-79%). Revisa gastos recurrentes.`;
    }
    return `${area.nombre} (${pct.toFixed(1)}%): Ejecucion baja (0%-49%). Margen disponible.`;
  }

  function dibujarGraficoUmbrales(canvas) {
    const ctx = canvas.getContext("2d");
    const w = canvas.width = canvas.parentElement.clientWidth - 8 || 460;
    const h = canvas.height = 190;
    ctx.clearRect(0, 0, w, h);

    if (!state.areas.length) {
      ctx.fillStyle = "#9ca3af";
      ctx.font = "12px sans-serif";
      ctx.fillText("Sin departamentos con presupuesto registrados aun.", 16, h / 2);
      return;
    }

    const maxPct = Math.max(115, ...state.areas.map(a => {
      const p = a.presupuesto > 0 ? (a.gasto * 100) / a.presupuesto : 0;
      return p + 10;
    }));
    const barW = Math.min(58, Math.max(26, (w - 75) / state.areas.length - 16));

    [80, 100].forEach((mark, idx) => {
      const y = h - 26 - (mark / maxPct) * (h - 46);
      ctx.save();
      ctx.setLineDash([5, 4]);
      ctx.strokeStyle = idx === 0 ? "#f59e0b" : "#ef4444";
      ctx.beginPath();
      ctx.moveTo(38, y);
      ctx.lineTo(w - 10, y);
      ctx.stroke();
      ctx.fillStyle = idx === 0 ? "#fbbf24" : "#f87171";
      ctx.font = "10px sans-serif";
      ctx.fillText(mark + "%", 4, y + 3);
      ctx.restore();
    });

    state.areas.forEach((a, i) => {
      const pct = a.presupuesto > 0 ? (a.gasto * 100) / a.presupuesto : 0;
      const bh = (Math.min(maxPct, pct) / maxPct) * (h - 46);
      const x = 48 + i * (barW + 16);
      const y = h - 26 - bh;
      ctx.fillStyle = pct >= 100 ? "#ef4444" : (pct >= 80 ? "#f59e0b" : "#8b5cf6");
      ctx.fillRect(x, y, barW, bh);
      ctx.fillStyle = "#e5e7eb";
      ctx.font = "11px sans-serif";
      ctx.fillText(a.nombre.slice(0, 9), x, h - 8);
      ctx.fillText(pct.toFixed(0) + "%", x, Math.max(11, y - 4));
    });
  }

  document.addEventListener("DOMContentLoaded", () => {
    const container = document.createElement("section");
    container.className = "ft-main-wrap";
    container.innerHTML = `
      <div class="ft-header">
        <div>
          <strong style="font-size:15px;">FinTrack Pro</strong>
          <span style="font-size:12px;color:#a78bfa;margin-left:8px;">Ciclo: <strong id="ft-ciclo-txt">${state.ciclo}</strong></span>
        </div>
        <div>
          <button type="button" class="ft-btn-ghost" id="ft-btn-ciclo">Cerrar / Abrir Ciclo</button>
          <button type="button" class="ft-btn-ghost" id="ft-btn-csv">Exportar CSV</button>
        </div>
      </div>

      <div class="ft-layout">
        <div class="ft-card">
          <div style="font-size:14px;font-weight:700;">Registrar Gasto / Monto</div>
          <div style="font-size:12px;color:#9ca3af;margin-top:2px;">
            Ingresa el monto y los datos del gasto; se clasifica por tramo y respeta los umbrales de 80% y 100%.
          </div>

          <div class="ft-form-grid">
            <input id="ft-in-area" class="ft-input" placeholder="Departamento / Area">
            <input id="ft-in-pres" class="ft-input" type="number" step="0.01" placeholder="Presupuesto area (opcional)">
            <input id="ft-in-monto" class="ft-input" type="number" step="0.01" placeholder="Monto del gasto">
            <input id="ft-in-cat" class="ft-input" placeholder="Categoria / Concepto">
          </div>

          <div style="display:flex;justify-content:space-between;align-items:center;margin-top:10px;gap:8px;flex-wrap:wrap;">
            <span id="ft-tramo-preview" style="font-size:12px;color:#c4b5fd;">Tramo estimado: DIMINUTO</span>
            <button type="button" class="ft-btn" id="ft-btn-guardar-gasto">Guardar Gasto</button>
          </div>

          <div style="margin-top:14px;font-size:13px;font-weight:700;">Movimientos y Aprobaciones</div>
          <div id="ft-movs-box"></div>
        </div>

        <div style="display:flex;flex-direction:column;gap:14px;">
          <div class="ft-card">
            <div style="font-size:13.5px;font-weight:700;margin-bottom:6px;">Grafico de Umbrales (80% Alerta / 100% Limite)</div>
            <canvas id="ft-chart-umbrales"></canvas>
            <div style="margin-top:10px;font-size:12.5px;font-weight:700;">Recomendaciones por % de Gasto</div>
            <div id="ft-recs-box" style="margin-top:6px;"></div>
          </div>

          <div class="ft-card">
            <div style="font-size:13.5px;font-weight:700;">Reglas de Area (Gasto Recurrente y Excepcion 100%)</div>
            <div id="ft-reglas-box"></div>
            <div style="margin-top:12px;font-size:13px;font-weight:700;">Auditoria</div>
            <div id="ft-audit-box"></div>
          </div>
        </div>
      </div>
    `;

    document.body.appendChild(container);
    const navBar = document.createElement("div");
    navBar.style.cssText = "display:flex;flex-wrap:wrap;gap:6px;margin-bottom:12px;";
    const vistas = [
      ["Inicio", "index.html"],
      ["Admin", "admin.html"],
      ["Financiero", "financiero.html"],
      ["Empleado", "empleado.html"],
      ["Notas", "notas.html"]
    ];
    vistas.forEach(([label, href]) => {
      const a = document.createElement("a");
      a.href = href;
      a.textContent = label;
      const active = location.pathname.endsWith("/" + href) || location.pathname.endsWith(href);
      a.style.cssText = active
        ? "padding:5px 10px;border-radius:7px;font-size:12px;font-weight:700;text-decoration:none;background:#7c3aed;color:#fff;"
        : "padding:5px 10px;border-radius:7px;font-size:12px;font-weight:600;text-decoration:none;background:rgba(139,92,246,0.14);color:#e9d5ff;border:1px solid rgba(168,85,247,0.3);";
      navBar.appendChild(a);
    });
    container.insertBefore(navBar, container.firstChild);

    const montoInput = document.getElementById("ft-in-monto");
    const tramoPreview = document.getElementById("ft-tramo-preview");
    montoInput.addEventListener("input", () => {
      const m = parseFloat(montoInput.value || "0");
      tramoPreview.textContent = "Tramo estimado: " + obtenerTramo(m);
    });

    function render() {
      document.getElementById("ft-ciclo-txt").textContent = state.ciclo;
      dibujarGraficoUmbrales(document.getElementById("ft-chart-umbrales"));

      const recsBox = document.getElementById("ft-recs-box");
      if (!state.areas.length) {
        recsBox.innerHTML = `<div class="ft-empty">Aun no hay areas registradas para generar recomendaciones por porcentaje.</div>`;
      } else {
        recsBox.innerHTML = state.areas.map(a => `<div style="font-size:12px;margin-bottom:4px;">• ${recomendacionArea(a)}</div>`).join("");
      }

      const movsBox = document.getElementById("ft-movs-box");
      if (!state.movimientos.length) {
        movsBox.innerHTML = `<div class="ft-empty">No hay movimientos registrados todavia.</div>`;
      } else {
        movsBox.innerHTML = `
          <table class="ft-table">
            <thead><tr><th>Area</th><th>Concepto</th><th>Tramo</th><th>Monto</th><th>Estado</th><th>Accion</th></tr></thead>
            <tbody>
              ${state.movimientos.map((m, idx) => `
                <tr>
                  <td>${m.area}</td>
                  <td>${m.concepto}</td>
                  <td>${m.tramo}</td>
                  <td>$${Number(m.monto).toFixed(2)}</td>
                  <td>${m.estado}</td>
                  <td>
                    <button type="button" class="ft-btn-ghost" onclick="window.__ftSetEstado(${idx},'APROBADO')">Aprobar</button>
                    <button type="button" class="ft-btn-ghost" onclick="window.__ftSetEstado(${idx},'RECHAZADO')">Rechazar</button>
                  </td>
                </tr>
              `).join("")}
            </tbody>
          </table>`;
      }

      const reglasBox = document.getElementById("ft-reglas-box");
      if (!state.areas.length) {
        reglasBox.innerHTML = `<div class="ft-empty">Sin reglas configuradas todavia.</div>`;
      } else {
        reglasBox.innerHTML = `
          <table class="ft-table">
            <thead><tr><th>Area</th><th>Gasto / Presup.</th><th>Recurrente</th><th>100%</th></tr></thead>
            <tbody>
              ${state.areas.map((a, idx) => `
                <tr>
                  <td>${a.nombre}</td>
                  <td>$${Number(a.gasto).toFixed(0)} / $${Number(a.presupuesto).toFixed(0)}</td>
                  <td>
                    <input class="ft-input" style="width:76px;padding:4px 6px;font-size:11.5px;" type="number" value="${a.recurrente || 0}" onchange="window.__ftSetRec(${idx}, this.value)">
                  </td>
                  <td>
                    <button type="button" class="ft-btn-ghost" onclick="window.__ftToggleExc(${idx})">${a.excepcion100 ? "Excepcion ON" : "Bloqueo ON"}</button>
                  </td>
                </tr>
              `).join("")}
            </tbody>
          </table>`;
      }

      const auditBox = document.getElementById("ft-audit-box");
      if (!state.auditoria.length) {
        auditBox.innerHTML = `<div class="ft-empty">Sin registros de auditoria.</div>`;
      } else {
        auditBox.innerHTML = `
          <table class="ft-table">
            <thead><tr><th>Hora</th><th>Accion</th><th>Detalle</th></tr></thead>
            <tbody>
              ${state.auditoria.slice(0, 6).map(x => `<tr><td>${x.fecha}</td><td>${x.accion}</td><td>${x.detalle}</td></tr>`).join("")}
            </tbody>
          </table>`;
      }
    }

    window.__ftSetEstado = (idx, nuevoEstado) => {
      const mov = state.movimientos[idx];
      if (!mov) return;
      const anterior = mov.estado;
      mov.estado = nuevoEstado;
      const areaObj = state.areas.find(a => a.nombre === mov.area);
      if (areaObj) {
        if (anterior !== "APROBADO" && nuevoEstado === "APROBADO") {
          areaObj.gasto = Number(areaObj.gasto || 0) + Number(mov.monto || 0);
        } else if (anterior === "APROBADO" && nuevoEstado !== "APROBADO") {
          areaObj.gasto = Math.max(0, Number(areaObj.gasto || 0) - Number(mov.monto || 0));
        }
      }
      registrarAuditoria("ESTADO_MOVIMIENTO", `${mov.area} ($${mov.monto}) -> ${nuevoEstado}`);
      render();
    };

    window.__ftSetRec = (idx, val) => {
      if (!state.areas[idx]) return;
      state.areas[idx].recurrente = Math.max(0, parseFloat(val || "0"));
      registrarAuditoria("RECURRENTE_AREA", `${state.areas[idx].nombre}: $${state.areas[idx].recurrente}/mes`);
      render();
    };

    window.__ftToggleExc = (idx) => {
      if (!state.areas[idx]) return;
      state.areas[idx].excepcion100 = !state.areas[idx].excepcion100;
      registrarAuditoria("EXCEPCION_100", `${state.areas[idx].nombre}: ${state.areas[idx].excepcion100}`);
      render();
    };

    document.getElementById("ft-btn-guardar-gasto").onclick = () => {
      const nombreArea = (document.getElementById("ft-in-area").value || "").trim();
      const presInput = parseFloat(document.getElementById("ft-in-pres").value || "0");
      const monto = parseFloat(document.getElementById("ft-in-monto").value || "0");
      const concepto = (document.getElementById("ft-in-cat").value || "").trim() || "Gasto general";

      if (!nombreArea || isNaN(monto) || monto <= 0) return;

      let areaObj = state.areas.find(a => a.nombre.toLowerCase() === nombreArea.toLowerCase());
      if (!areaObj) {
        areaObj = {
          nombre: nombreArea,
          presupuesto: presInput > 0 ? presInput : monto,
          gasto: 0,
          recurrente: 0,
          excepcion100: false
        };
        state.areas.push(areaObj);
      } else if (presInput > 0) {
        areaObj.presupuesto = presInput;
      }

      const pctProyectado = areaObj.presupuesto > 0 ? ((areaObj.gasto + monto) * 100) / areaObj.presupuesto : 0;
      const excedeSinExcepcion = pctProyectado > 100 && !areaObj.excepcion100;
      const estadoInicial = excedeSinExcepcion ? "PENDIENTE" : "APROBADO";

      if (estadoInicial === "APROBADO") {
        areaObj.gasto = Number(areaObj.gasto || 0) + monto;
      }

      state.movimientos.unshift({
        area: areaObj.nombre,
        concepto,
        monto,
        tramo: obtenerTramo(monto),
        estado: estadoInicial
      });

      registrarAuditoria(
        excedeSinExcepcion ? "PENDIENTE_100" : "GASTO_REGISTRADO",
        `${areaObj.nombre}: $${monto} (${estadoInicial})`
      );

      document.getElementById("ft-in-monto").value = "";
      document.getElementById("ft-in-cat").value = "";
      tramoPreview.textContent = "Tramo estimado: DIMINUTO";
      render();
    };

    document.getElementById("ft-btn-ciclo").onclick = () => {
      const [y, m] = state.ciclo.split("-").map(Number);
      const nextM = m === 12 ? 1 : m + 1;
      const nextY = m === 12 ? y + 1 : y;
      const prev = state.ciclo;
      state.ciclo = `${nextY}-${String(nextM).padStart(2, "0")}`;
      state.areas.forEach(a => {
        a.gasto = Number(a.recurrente || 0);
        a.excepcion100 = false;
      });
      registrarAuditoria("CIERRE_CICLO", `${prev} -> ${state.ciclo}`);
      render();
    };

    document.getElementById("ft-btn-csv").onclick = () => {
      const rows = ["Area,Presupuesto,Gasto,Recurrente,Excepcion100"].concat(
        state.areas.map(a => `"${a.nombre}",${a.presupuesto},${a.gasto},${a.recurrente || 0},${Boolean(a.excepcion100)}`)
      );
      const blob = new Blob([rows.join("\n")], { type: "text/csv;charset=utf-8;" });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "fintrack_reporte.csv";
      link.click();
      URL.revokeObjectURL(url);
      registrarAuditoria("EXPORTAR_CSV", "Reporte CSV generado");
      render();
    };

    render();
  });
})();