let token = localStorage.getItem('fintrack_token');
let currentRole = localStorage.getItem('fintrack_role');
let currentUser = localStorage.getItem('fintrack_user');
let chartInstance = null;

function fillCreds(email, pass) {
    document.getElementById('emailInput').value = email;
    document.getElementById('passInput').value = pass;
}

function showAlert(msg, isError = false) {
    const banner = document.getElementById('alertBanner');
    banner.textContent = msg;
    banner.className = isError
        ? 'p-4 rounded-xl border text-sm font-medium bg-rose-950/60 border-rose-500/40 text-rose-200'
        : 'p-4 rounded-xl border text-sm font-medium bg-amber-950/60 border-amber-500/40 text-amber-200';
    banner.classList.remove('hidden');
    setTimeout(() => banner.classList.add('hidden'), 7000);
}

async function apiFetch(url, options = {}) {
    options.headers = {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
        ...options.headers
    };
    const res = await fetch(url, options);
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || 'Error en la operación');
    return data;
}

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errEl = document.getElementById('loginError');
    errEl.classList.add('hidden');
    try {
        const data = await apiFetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                email: document.getElementById('emailInput').value,
                password: document.getElementById('passInput').value
            })
        });
        token = data.token;
        currentRole = data.role;
        currentUser = data.nombre;
        localStorage.setItem('fintrack_token', token);
        localStorage.setItem('fintrack_role', currentRole);
        localStorage.setItem('fintrack_user', currentUser);
        initDashboard();
    } catch (err) {
        errEl.textContent = err.message;
        errEl.classList.remove('hidden');
    }
});

function logout() {
    localStorage.clear();
    token = null;
    document.getElementById('dashboardView').classList.add('hidden');
    document.getElementById('loginView').classList.remove('hidden');
}

async function initDashboard() {
    if (!token) return;
    document.getElementById('loginView').classList.add('hidden');
    document.getElementById('dashboardView').classList.remove('hidden');
    document.getElementById('userBadge').textContent = `${currentUser} · ${currentRole}`;
    await refreshData();
}

async function refreshData() {
    try {
        const [deptos, txs] = await Promise.all([
            apiFetch('/api/departamentos'),
            apiFetch('/api/transacciones')
        ]);

        const grid = document.getElementById('deptosGrid');
        const select = document.getElementById('txDepto');
        grid.innerHTML = '';
        select.innerHTML = '';

        deptos.forEach(d => {
            const pct = Math.min(d.porcentajeUso, 100);
            const color = d.estado === 'EXCEDIDO_100' ? 'bg-rose-500'
                        : d.estado === 'ADVERTENCIA_80' ? 'bg-amber-500' : 'bg-emerald-500';

            grid.innerHTML += `
                <div class="bg-slate-950 border border-slate-800 rounded-xl p-4">
                    <div class="flex justify-between items-start mb-2">
                        <div>
                            <h4 class="font-semibold text-white text-sm">${d.nombre}</h4>
                            <p class="text-[11px] text-slate-400">ID: ${d.id} · ${d.bloquearAlExceder ? 'Bloqueo estricto' : 'Flexible'}</p>
                        </div>
                        <span class="text-xs font-bold text-slate-200">${d.porcentajeUso}%</span>
                    </div>
                    <div class="w-full bg-slate-800 h-2 rounded-full overflow-hidden mb-2">
                        <div class="${color} h-full" style="width: ${pct}%"></div>
                    </div>
                    <p class="text-xs text-slate-400">S/ ${d.gastoAcumulado.toFixed(2)} / <span class="text-slate-200">S/ ${d.presupuestoMensual.toFixed(2)}</span></p>
                </div>
            `;
            select.innerHTML += `<option value="${d.id}">${d.nombre} (ID: ${d.id})</option>`;
        });

        // Actualizar Chart.js
        const ctx = document.getElementById('budgetChart').getContext('2d');
        if (chartInstance) chartInstance.destroy();
        chartInstance = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: deptos.map(d => d.nombre),
                datasets: [{
                    data: deptos.map(d => d.gastoAcumulado),
                    backgroundColor: ['#10b981', '#f59e0b', '#0ea5e9', '#8b5cf6'],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'right', labels: { color: '#cbd5e1', font: { size: 11 } } } }
            }
        });

        // Tabla de transacciones
        const tbody = document.getElementById('txTableBody');
        tbody.innerHTML = txs.slice().reverse().map(t => `
            <tr>
                <td class="py-2.5 font-medium text-white">${t.departamentoNombre}</td>
                <td class="py-2.5"><span class="px-2 py-0.5 rounded text-[10px] font-bold ${t.tipo === 'EGRESO' ? 'bg-rose-500/20 text-rose-300' : 'bg-emerald-500/20 text-emerald-300'}">${t.tipo}</span></td>
                <td class="py-2.5 text-slate-300">${t.categoria}</td>
                <td class="py-2.5 text-slate-400">${t.descripcion}</td>
                <td class="py-2.5 font-semibold text-white">S/ ${Number(t.monto).toFixed(2)}</td>
                <td class="py-2.5 text-amber-300">${t.alertaGenerada || '—'}</td>
            </tr>
        `).join('');
    } catch (err) {
        if (err.message.includes('401')) logout();
    }
}

document.getElementById('txForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const res = await apiFetch('/api/transacciones', {
            method: 'POST',
            body: JSON.stringify({
                departamentoId: document.getElementById('txDepto').value,
                tipo: document.getElementById('txTipo').value,
                monto: document.getElementById('txMonto').value,
                categoria: document.getElementById('txCategoria').value,
                descripcion: document.getElementById('txDesc').value
            })
        });
        if (res.alertaGenerada) showAlert(res.alertaGenerada, false);
        e.target.reset();
        await refreshData();
    } catch (err) {
        showAlert(err.message, true);
    }
});

document.getElementById('deptoForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await apiFetch('/api/departamentos', {
            method: 'POST',
            body: JSON.stringify({
                nombre: document.getElementById('depNombre').value,
                presupuestoMensual: document.getElementById('depPresupuesto').value,
                bloquearAlExceder: document.getElementById('depBloquear').checked
            })
        });
        e.target.reset();
        await refreshData();
    } catch (err) {
        showAlert('Solo ROLE_ADMIN puede crear departamentos o datos inválidos.', true);
    }
});

document.getElementById('aiForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const box = document.getElementById('aiResponseBox');
    box.textContent = 'Invocando herramienta consultarPresupuestosTool()...';
    try {
        const res = await apiFetch('/api/copilot/chat', {
            method: 'POST',
            body: JSON.stringify({ pregunta: document.getElementById('aiPrompt').value })
        });
        box.textContent = `[Modo: ${res.modo}]\n\n${res.respuesta}`;
    } catch (err) {
        box.textContent = 'Error o rol sin permisos para consultar el Copiloto IA (requiere ADMIN o FINANCIERO).';
    }
});

if (token) initDashboard();