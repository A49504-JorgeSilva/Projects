import { fetchOptions, generate } from './api.js';
import {
    populateObjectives,
    updateMaterialTypes,
    updateContentInput,
    updateSubmitState,
    setLoading,
    showResult,
    showError
} from './ui.js';

let allOptions = [];

function onObjectiveChange() {
    const objective = document.getElementById('objective').value;
    updateMaterialTypes(allOptions, objective);
    updateContentInput('');
    updateSubmitState();
}

function onMaterialTypeChange() {
    updateContentInput(document.getElementById('material-type').value);
    updateSubmitState();
}

async function handleSubmit(event) {
    event.preventDefault();

    const objective = document.getElementById('objective').value;
    const materialType = document.getElementById('material-type').value;

    const formData = new FormData();
    formData.append('objective', objective);
    formData.append('materialType', materialType);

    if (materialType === 'REPO') {
        const url = document.getElementById('content-text')?.value.trim();
        if (!url) return;
        formData.append('content', url);
    } else {
        const file = document.getElementById('content-file')?.files[0];
        if (!file) return;
        formData.append('content', file);
    }

    setLoading(true);
    try {
        const text = await generate(formData);
        showResult(text);
    } catch (err) {
        showError(err.message);
    } finally {
        setLoading(false);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const data = await fetchOptions();
        allOptions = data.options;
        populateObjectives(allOptions);
    } catch {
        showError('Erro ao carregar opções do servidor.');
    }

    document.getElementById('objective').addEventListener('change', onObjectiveChange);
    document.getElementById('material-type').addEventListener('change', onMaterialTypeChange);
    document.getElementById('generation-form').addEventListener('submit', handleSubmit);
});
