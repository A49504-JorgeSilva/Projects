# App — Gerador de Material Pedagógico

Aplicação web que automatiza a geração de material pedagógico a partir de ficheiros ou URLs, usando a API de um LLM (Claude, DeepSeek ou Mistral).

A arquitetura detalhada está documentada em [docs/arquitetura-app.md](docs/arquitetura-app.md).

---

## Requisitos

- JDK 21+
- Gradle Wrapper incluído (`gradlew`) — não é necessário instalar o Gradle

---

## Como correr

### Sem API (modo de teste)

```powershell
.\gradlew run
```

Usa o `FakeLlmClient` — devolve uma resposta simulada sem chamar nenhuma API externa.

### Com Claude (Anthropic)

```powershell
$env:LLM_PROVIDER = "anthropic"
$env:LLM_API_KEY  = "sk-ant-..."
$env:LLM_MODEL    = "claude-opus-4-8"
.\gradlew run
```

Obter chave: [console.anthropic.com](https://console.anthropic.com) → API Keys

### Com DeepSeek

```powershell
$env:LLM_PROVIDER = "openai-compatible"
$env:LLM_API_KEY  = "sk-..."
$env:LLM_BASE_URL = "https://api.deepseek.com/v1"
$env:LLM_MODEL    = "deepseek-chat"
.\gradlew run
```

Obter chave: [platform.deepseek.com](https://platform.deepseek.com) → API Keys

### Com Mistral (LeChat)

```powershell
$env:LLM_PROVIDER = "openai-compatible"
$env:LLM_API_KEY  = "..."
$env:LLM_BASE_URL = "https://api.mistral.ai/v1"
$env:LLM_MODEL    = "mistral-large-latest"
.\gradlew run
```

Obter chave: [console.mistral.ai](https://console.mistral.ai) → API Keys (tem plano gratuito)

---

Após arrancar, aceder em `http://localhost:8080`.

As variáveis de ambiente só existem na sessão atual do terminal — se abrir um terminal novo tem de as definir outra vez.

---

## Correr os testes

```powershell
.\gradlew test
```

Relatório HTML: `build/reports/tests/test/index.html`

---

## Variáveis de ambiente

| Variável       | Default    | Descrição                                               |
|----------------|------------|---------------------------------------------------------|
| `LLM_PROVIDER` | `fake`     | Provider: `anthropic`, `openai-compatible`              |
| `LLM_API_KEY`  | —          | Chave de API do provider escolhido                      |
| `LLM_MODEL`    | —          | Modelo (ex.: `claude-opus-4-8`, `deepseek-chat`)        |
| `LLM_BASE_URL` | —          | URL base (só para `openai-compatible`)                  |
| `PROMPTS_DIR`  | `prompts`  | Diretório com os ficheiros de prompt                    |
