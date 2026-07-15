# Arquitetura da App de Geração de Material Pedagógico

## 1. Objetivo

Ferramenta web que automatiza a geração de material pedagógico via API de um LLM. O utilizador escolhe o objetivo e o tipo de material, fornece o conteúdo (ficheiro ou URL), e a app seleciona o prompt correto e devolve o resultado gerado.

Não é um produto final — é um protótipo que operacionaliza os prompts validados no estudo exploratório (PE, SegInf, IPW).

---

## 2. Fluxo funcional

```
1. Browser carrega GET /options
   → devolve as combinações válidas objetivo + tipo de material

2. Utilizador escolhe:
   - Objetivo: TEACHING_MATERIAL | SUMMARY | QUIZ
   - Tipo de material (lista filtrada pelo objetivo escolhido)
   - Conteúdo: ficheiro (.pdf, .txt, .md) ou URL (para REPO)

3. Browser envia POST /generate com multipart:
   - objective, materialType, content (ficheiro ou texto)

4. Servidor extrai o texto do conteúdo (PDFBox se for .pdf)

5. PromptRouterService seleciona o ficheiro de prompt correto

6. LlmClient envia (system prompt + conteúdo) ao provider configurado

7. Resultado é devolvido ao browser em JSON
```

### Mapeamento objetivo × tipo → prompt

| Objetivo           | Tipo de Material | Ficheiro de prompt         |
|--------------------|------------------|----------------------------|
| TEACHING_MATERIAL  | FUC              | `prompt_fuc.md`            |
| SUMMARY            | SLIDES           | `prompt_summary_slides.md` |
| SUMMARY            | NOTES            | `prompt_summary_notes.md`  |
| SUMMARY            | TEST             | `prompt_summary_test.md`   |
| SUMMARY            | REPO             | `prompt_summary_repo.md`   |
| QUIZ               | SLIDES           | `prompt_quiz.md`           |
| QUIZ               | NOTES            | `prompt_quiz.md`           |

Combinações não listadas (ex.: QUIZ + REPO) são rejeitadas com erro.

---

## 3. Arquitetura em camadas

A app segue uma arquitetura de 4 camadas: **Domain → Services → WebApi → Server**. Cada camada só depende das camadas abaixo, nunca das de cima.

```
┌─────────────────────────────────┐
│           Server                │  Application.kt — entrypoint, wiring
├─────────────────────────────────┤
│           WebApi                │  Rotas Ktor, DTOs, validação de input
├─────────────────────────────────┤
│          Services               │  Lógica de negócio, cliente LLM
├─────────────────────────────────┤
│           Domain                │  Modelos puros, sem dependências externas
└─────────────────────────────────┘
```

### Domain (`isel.ps.domain`)

Modelos puros — sem Ktor, sem HTTP, sem I/O.

| Ficheiro              | Conteúdo                                                                 |
|-----------------------|--------------------------------------------------------------------------|
| `Objective.kt`        | Enum: `TEACHING_MATERIAL`, `SUMMARY`, `QUIZ`                             |
| `MaterialType.kt`     | Enum: `FUC`, `SLIDES`, `NOTES`, `TEST`, `REPO`                           |
| `PromptMapping.kt`    | `data class PromptMapping` + lista `PROMPT_MAPPINGS` com os 7 mapeamentos |
| `GenerationModels.kt` | `GenerationRequest` (input) e `GenerationResult` (output)                |

### Services (`isel.ps.services`)

Lógica de orquestração e integração com providers externos.

| Ficheiro                       | Responsabilidade                                                          |
|--------------------------------|---------------------------------------------------------------------------|
| `LlmClient.kt`                 | Interface: `generate(systemPrompt, userContent): String`                  |
| `AnthropicClientImpl.kt`       | Implementação para a API Anthropic (`POST /v1/messages`)                  |
| `OpenAICompatibleClientImpl.kt`| Implementação para qualquer API OpenAI-compatible (DeepSeek, Mistral)     |
| `FakeLlmClient.kt`             | Implementação stub para testes sem API real                               |
| `PromptRouterService.kt`       | Recebe `Objective` + `MaterialType`, lê e devolve o ficheiro de prompt    |
| `GenerationService.kt`         | Orquestra: router → LlmClient → resultado                                 |

#### Suporte a múltiplos providers

A interface `LlmClient` desacopla a lógica de negócio do provider concreto. O cliente é instanciado no arranque com base em variáveis de ambiente — sem tocar no código para trocar de provider.

```
LlmClient (interface)
├── AnthropicClientImpl       → API Anthropic  (formato próprio)
├── OpenAICompatibleClientImpl → DeepSeek, Mistral (formato OpenAI-compatible)
└── FakeLlmClient             → Testes locais sem API
```

O `AnthropicClientImpl` usa o formato próprio da Anthropic (`system` separado das `messages`). O `OpenAICompatibleClientImpl` usa o formato OpenAI (`messages` com role `system` e `user`), compatível com DeepSeek e Mistral sem alterações.

### WebApi (`isel.ps.webapi`)

Camada Ktor — expõe a API HTTP, valida o input, converte entre DTOs e modelos de domínio.

| Ficheiro            | Responsabilidade                                                         |
|---------------------|--------------------------------------------------------------------------|
| `Dtos.kt`           | `GenerationResultDto`, `OptionsDto`, `OptionEntryDto`                    |
| `OpcoesRoutes.kt`   | `GET /options` — devolve as combinações válidas para os dropdowns        |
| `GeracaoRoutes.kt`  | `POST /generate` — recebe multipart, extrai texto (PDFBox se `.pdf`), chama `GenerationService` |

O `POST /generate` aceita `multipart/form-data` com:
- `objective` (form field) — valor do enum `Objective`
- `materialType` (form field) — valor do enum `MaterialType`
- `content` (form field com texto, ou file item com ficheiro)

### Server (`isel.ps.server`)

| Ficheiro          | Responsabilidade                                                           |
|-------------------|----------------------------------------------------------------------------|
| `Application.kt`  | Entrypoint (`EngineMain`), wiring de dependências, plugins Ktor, rotas     |

O `Application.kt` lê as variáveis de ambiente e instancia o cliente LLM correto através da função `createLlmClient()`. Não usa framework de injeção de dependências — o wiring é manual, adequado ao tamanho do projeto.

Plugins Ktor configurados: `ContentNegotiation` (JSON), `CORS`, `staticResources` (frontend).

### Frontend (`src/main/resources/static/`)

Servido como ficheiros estáticos pelo próprio Ktor, eliminando CORS entre frontend e backend.

| Ficheiro      | Responsabilidade                                                        |
|---------------|-------------------------------------------------------------------------|
| `index.html`  | Estrutura HTML — formulário, áreas de resultado e erro                  |
| `style.css`   | Estilos                                                                 |
| `labels.js`   | Mapeamentos dos enums para português (ex.: `TEACHING_MATERIAL` → `"Material Pedagógico"`) |
| `api.js`      | Funções `fetchOptions()` e `generate()` — chamadas fetch ao servidor    |
| `ui.js`       | Manipulação do DOM — popula selects, mostra resultado/erro, loading     |
| `app.js`      | Entry point — estado (`allOptions`), event listeners, `handleSubmit`    |

Usa ES modules nativos (`type="module"`), sem bundler nem framework.

---

## 4. Configuração por variáveis de ambiente

| Variável       | Obrigatória          | Descrição                                      | Exemplo                          |
|----------------|----------------------|------------------------------------------------|----------------------------------|
| `LLM_PROVIDER` | Não (default: fake)  | Provider a usar                                | `anthropic`, `openai-compatible` |
| `LLM_API_KEY`  | Se provider ≠ fake   | Chave de API do provider                       | `sk-ant-...`                     |
| `LLM_MODEL`    | Se provider ≠ fake   | Modelo a usar                                  | `claude-opus-4-8`                |
| `LLM_BASE_URL` | Se `openai-compatible` | URL base da API (sem `/chat/completions`)    | `https://api.deepseek.com/v1`    |
| `PROMPTS_DIR`  | Não (default: `prompts`) | Diretório com os ficheiros de prompt       | `prompts`                        |

Se `LLM_PROVIDER` não estiver definido, o servidor arranca com o `FakeLlmClient` (sem chamadas reais à API).

---

## 5. Stack técnico

| Componente          | Tecnologia                        | Versão   |
|---------------------|-----------------------------------|----------|
| Linguagem           | Kotlin (JVM)                      | 2.0.21   |
| Build               | Gradle                            | 8.12     |
| Framework web       | Ktor (Netty)                      | 3.0.3    |
| Serialização JSON   | kotlinx.serialization             | 1.7.3    |
| Cliente HTTP        | Ktor Client (CIO)                 | 3.0.3    |
| Extração de PDF     | Apache PDFBox                     | 3.0.3    |
| Logging             | Logback (SLF4J)                   | 1.5.12   |
| Testes — mocking    | MockK                             | 1.13.13  |
| Testes — coroutines | kotlinx-coroutines-test           | 1.9.0    |
| Frontend            | HTML + JS vanilla (ES modules)    | —        |

---

## 6. Estrutura de pastas

```
app/
├── docs/
│   └── arquitetura-app.md
├── prompts/
│   ├── prompt_fuc.md
│   ├── prompt_summary_slides.md
│   ├── prompt_summary_notes.md
│   ├── prompt_summary_test.md
│   ├── prompt_summary_repo.md
│   └── prompt_quiz.md
├── src/
│   ├── main/
│   │   ├── kotlin/isel/ps/
│   │   │   ├── domain/
│   │   │   │   ├── Objective.kt
│   │   │   │   ├── MaterialType.kt
│   │   │   │   ├── PromptMapping.kt
│   │   │   │   └── GenerationModels.kt
│   │   │   ├── services/
│   │   │   │   ├── LlmClient.kt
│   │   │   │   ├── AnthropicClientImpl.kt
│   │   │   │   ├── OpenAICompatibleClientImpl.kt
│   │   │   │   ├── FakeLlmClient.kt
│   │   │   │   ├── PromptRouterService.kt
│   │   │   │   └── GenerationService.kt
│   │   │   ├── webapi/
│   │   │   │   ├── Dtos.kt
│   │   │   │   ├── OpcoesRoutes.kt
│   │   │   │   └── GeracaoRoutes.kt
│   │   │   └── server/
│   │   │       └── Application.kt
│   │   └── resources/
│   │       ├── application.conf
│   │       └── static/
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── labels.js
│   │           ├── api.js
│   │           ├── ui.js
│   │           └── app.js
│   └── test/
│       └── kotlin/isel/ps/
│           ├── services/
│           │   ├── PromptRouterServiceTest.kt
│           │   └── GenerationServiceTest.kt
│           └── webapi/
│               ├── OpcoesRoutesTest.kt
│               └── GeracaoRoutesTest.kt
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 7. Testes

Testes unitários nas camadas de Services e WebApi. Os clientes LLM reais (`AnthropicClientImpl`, `OpenAICompatibleClientImpl`) não são testados unitariamente — são adaptadores HTTP finos sem lógica de negócio própria.

| Ficheiro de teste          | O que testa                                               | Testes |
|----------------------------|-----------------------------------------------------------|--------|
| `PromptRouterServiceTest`  | Resolução de prompts, combinações inválidas, ficheiro em falta | 3  |
| `GenerationServiceTest`    | Orquestração, propagação de erros, interação com `LlmClient` (MockK) | 4 |
| `OpcoesRoutesTest`         | `GET /options` — status, número de opções, campos presentes | 3    |
| `GeracaoRoutesTest`        | `POST /generate` — casos válidos, campos em falta, captura do request | 6 |
| **Total**                  |                                                           | **16** |

Correr os testes:
```powershell
.\gradlew test
```

Relatório HTML: `build/reports/tests/test/index.html`
