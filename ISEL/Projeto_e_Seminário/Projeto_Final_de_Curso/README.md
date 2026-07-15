# Projeto-Seminário---ISEL

## Estrutura do repositório

- `materiais-fonte/` – Documentos fornecidos como entrada aos LLMs (slides, apontamentos, enunciados, código), organizados por UC.
- `prompts/` – Instruções completas fornecidas aos LLM (formato Markdown).
- `outputs/` – Materiais didáticos gerados pelos LLM, organizados por UC. Cada UC contém subpastas por tipo de material gerado (resumos e quizzes).
- `avaliacoes/` – Avaliações dos professores sobre a qualidade e fidelidade dos materiais gerados.
- `app/` – Aplicação web que automatiza o processo de geração de material pedagógico via API do Claude.


## 🎯 Objetivo do projeto

Avaliar de que forma diferentes LLMs, tipos de materiais de entrada (slides, apontamentos GitHub, enunciados de testes) e estratégias de _prompt engineering_ influenciam a **qualidade**, **fidelidade curricular** e **utilidade pedagógica** do material gerado. O estudo é exploratório e os resultados foram validados pelos docentes de cada UC.

## 🚀 Como reproduzir os resultados

1. **Escolha um prompt** na pasta `prompts/` de acordo com o tipo de material que pretende processar (PDF, GitHub, ficheiros carregados, testes, quizzes, Fichas de Unidade Curricular).
2. **Forneça ao modelo** o conteúdo da pasta correspondente em `materiais-fonte/` (ou o link do GitHub, quando aplicável).
3. **Cole o prompt** na interface de um dos modelos suportados (Claude, DeepSeek ou LeChat) e envie juntamente com os ficheiros.
4. **Guarde o output** na respectiva subpasta de `outputs/`, seguindo a mesma organização por UC.
5. **Para ver as avaliações**, consulte a pasta `avaliacoes/`.

## 📝 Notas importantes

- Todos os _prompts_ foram concebidos para forçar o modelo a **usar exclusivamente as fontes fornecidas**, evitando alucinações ou conhecimento externo.
- Os quizzes foram gerados exclusivamente a partir dos materiais originais dos docentes, nunca a partir dos resumos produzidos pelos LLMs, de forma a garantir a validade do que é avaliado.
- Sempre que um modelo não conseguiu processar a totalidade do material (ex: limitação de janela de contexto), essa informação está registada nos comentários dos ficheiros de `outputs/`.
- As avaliações dos docentes incluem classificações de qualidade e fidelidade, bem como observações qualitativas.
- As avaliações do orientador encontram-se em _sticky notes_ nos PDFs.

## 🖥️ Aplicação web (`app/`)

A pasta `app/` contém uma aplicação web desenvolvida em Kotlin/Ktor que serve de interface para geração automática de material pedagógico através da API do Claude.

### Requisitos

- JDK 21+
- Chave de API da Anthropic (`ANTHROPIC_API_KEY`)

### Como correr

```powershell
cd app
$env:ANTHROPIC_API_KEY = "sk-ant-..."
.\gradlew run
```

Aceder em `http://localhost:8080`.

### Funcionalidades

- Seleção de objetivo (Material Pedagógico, Resumo, Quiz) e tipo de material (FUC, Slides, Notas, Teste, Repositório GitHub)
- Upload de ficheiros `.pdf`, `.txt` e `.md`, ou introdução de URL para repositórios GitHub
- Geração de conteúdo via API do Claude (`claude-opus-4-8`)
- Interface web em vanilla JS com dois dropdowns dependentes

### Arquitetura

Aplicação em 4 camadas: **Domain → Services → WebApi → Server**, com testes unitários às camadas de serviços e WebApi.

---

## 🔧 Tecnologias e ferramentas

- **Modelos testados**: Claude (Anthropic), DeepSeek, LeChat (Mistral AI)
- **Formatos de entrada**: PDF, repositórios GitHub públicos, ficheiros Markdown, código fonte, Fichas de Unidade Curricular
- **Formatos de saída**: Markdown (`.md`) e PDF (`.pdf`)
- **Controlo de versão**: Git + GitHub

## 📄 Licença

Este repositório é parte de um trabalho académico. Os materiais dos docentes pertencem aos respetivos autores. Os _prompts_ e os _outputs_ gerados estão disponíveis para consulta, mas recomenda-se contactar o autor antes de qualquer utilização externa. Para mais informações, consulte o autor: **Jorge Silva** (a49504@alunos.isel.pt).

---

**Autor:** Jorge Silva  
**Orientadores:** Diego Passos, Fernanda Passos  
**Instituição:** ISEL – Instituto Superior de Engenharia de Lisboa  
**Data:** Junho de 2026