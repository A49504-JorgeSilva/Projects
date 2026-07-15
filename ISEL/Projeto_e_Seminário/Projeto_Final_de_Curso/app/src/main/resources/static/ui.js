import { OBJECTIVE_LABELS, MATERIAL_LABELS } from './labels.js';

export function populateObjectives(options) {
    const select = document.getElementById('objective');
    const objectives = [...new Set(options.map(o => o.objective))];
    objectives.forEach(obj => {
        const option = document.createElement('option');
        option.value = obj;
        option.textContent = OBJECTIVE_LABELS[obj] ?? obj;
        select.appendChild(option);
    });
}

export function updateMaterialTypes(options, objective) {
    const select = document.getElementById('material-type');
    select.innerHTML = '<option value="">Seleciona um tipo</option>';
    select.disabled = !objective;

    if (!objective) return;

    options
        .filter(o => o.objective === objective)
        .forEach(o => {
            const option = document.createElement('option');
            option.value = o.materialType;
            option.textContent = MATERIAL_LABELS[o.materialType] ?? o.materialType;
            select.appendChild(option);
        });
}

export function updateContentInput(materialType) {
    const group = document.getElementById('content-group');

    if (!materialType) {
        group.classList.add('hidden');
        group.innerHTML = '';
        return;
    }

    group.classList.remove('hidden');

    if (materialType === 'REPO') {
        group.innerHTML = `
            <label for="content-text">URL do Repositório</label>
            <input type="url" id="content-text" placeholder="https://github.com/utilizador/repo" required>
        `;
    } else {
        group.innerHTML = `
            <label for="content-file">Ficheiro</label>
            <input type="file" id="content-file" accept=".pdf,.md,.txt,.zip" required>
        `;
        document.getElementById('content-file').addEventListener('change', updateSubmitState);
    }
}

export function updateSubmitState() {
    const objective = document.getElementById('objective').value;
    const materialType = document.getElementById('material-type').value;
    document.getElementById('submit-btn').disabled = !objective || !materialType;
}

export function setLoading(isLoading) {
    const btn = document.getElementById('submit-btn');
    btn.disabled = isLoading;
    btn.textContent = isLoading ? 'A gerar…' : 'Gerar';

    if (isLoading) {
        document.getElementById('result-section').classList.add('hidden');
        document.getElementById('error-section').classList.add('hidden');
    }
}

export function showResult(text) {
    const resultSection = document.getElementById('result-section');
    document.getElementById('result-content').textContent = text;

    const downloadBtn = document.getElementById('download-btn');
    downloadBtn.onclick = () => {
        const blob = new Blob([text], { type: 'text/markdown' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'resultado.md';
        a.click();
        URL.revokeObjectURL(url);
    };

    resultSection.classList.remove('hidden');
    resultSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

export function showError(message) {
    const errorSection = document.getElementById('error-section');
    errorSection.textContent = message;
    errorSection.classList.remove('hidden');
}
