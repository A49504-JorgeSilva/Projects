# Prompt: Extração de matéria a partir de enunciados de testes

## Objetivo
A partir de vários enunciados de testes, o modelo deve identificar os conceitos e temas avaliados e produzir um resumo didático da disciplina, sem resolver os exercícios.

## Prompt

**Formato de entrega:** Resume o conteúdo num ficheiro Markdown (.md) com títulos hierárquicos, listas objetivas e negrito para informação crítica. Inclui no final uma lista de termos-chave.

**Tarefa:** Vou enviar-te vários testes de uma disciplina. O teu objetivo **não é resolver os exercícios** — é extrair a matéria que está por trás deles e produzir material didático estruturado.

Faz isto em duas fases:

**Fase 1 — Análise e mapeamento:** Lê todos os testes na íntegra. Identifica todos os temas, conceitos e tópicos avaliados em cada exercício. Cruza essa informação entre todos os testes para perceber quais os temas centrais da disciplina, quais aparecem com mais frequência, e qual a progressão lógica provável da matéria (do mais fundamental para o mais complexo).

**Fase 2 — Produção do material didático:** Com base na ordem que inferiste na Fase 1, produz um resumo da matéria que capture obrigatoriamente:
- Conceitos, definições e princípios fundamentais de cada tema
- Explicações claras e acessíveis, como se fosse um apontamento de estudo
- Fórmulas, regras ou propriedades relevantes (sem as omitir)
- Ligações entre temas — como se encadeiam logicamente

**Critérios de qualidade:**
- Não resolvas nem faças referência direta aos exercícios dos testes
- Fórmulas, definições formais e regras devem ser reproduzidas sem sumarização excessiva
- Se o mesmo conceito aparecer abordado de formas diferentes em testes distintos, consolida-o numa explicação única e completa
- Se não conseguires determinar a ordem lógica de um tema com confiança, coloca-o no final e sinaliza-o com _[ORDEM INCERTA]_
- Sempre que um conceito parecer incompleto ou ambíguo com base nos testes, sinaliza com _[VERIFICAR]_