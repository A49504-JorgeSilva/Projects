export async function fetchOptions() {
    const response = await fetch('/options');
    if (!response.ok) throw new Error(`Erro ${response.status} ao carregar opções.`);
    return response.json();
}

export async function generate(formData) {
    const response = await fetch('/generate', { method: 'POST', body: formData });
    if (!response.ok) throw new Error(`Erro ${response.status}: ${await response.text()}`);
    const data = await response.json();
    return data.text;
}
