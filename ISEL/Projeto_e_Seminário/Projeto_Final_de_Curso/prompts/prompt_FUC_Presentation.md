# Prompt: Geração de material de ensino através da FUC da cadeira

## Objetivo
A partir da FUC da Unidade Curricular, o modelo deve identificar os conceitos e temas a serem ensinados e produzir uma apresentação de forma a que se possa ser usada para ensino.

## Prompt

**Tema a desenvolver:** [insere aqui o tema/matéria específico que queres que o modelo desenvolva, conforme identificado na FUC]

**Formato de entrega:** Gera o conteúdo num ficheiro Markdown (.md) compatível com **Marp**. O ficheiro deve começar obrigatoriamente com o seguinte frontmatter:
---
marp: true
theme: default
paginate: true
---
Cada slide deve ser separado por uma linha `---` isolada. Cada slide tem um título (`##`) seguido do seu conteúdo em pontos objetivos. Usa **negrito** para conceitos críticos e blocos de código com a sintaxe adequada (` ```python `, ` ```js `, etc.) onde aplicável. Não uses títulos `#` (nível 1) dentro dos slides — reserva esse nível apenas para o título da apresentação no primeiro slide.

**Fonte de informação:** O conteúdo deve basear-se na bibliografia referenciada na FUC e no teu conhecimento sobre o tema indicado acima. A FUC serve para confirmar o nível de profundidade esperado e o enquadramento do tema dentro da UC — não é fonte de conteúdo. Sempre que recorreres a conhecimento geral não diretamente sustentado pela bibliografia da FUC, sinaliza com _[FONTE EXTERNA]_.

**Fase 1 — Mapeamento:**  
Lê a FUC na íntegra e localiza o tema indicado acima. Confirma o seu enquadramento na sequência pedagógica da UC, o nível de profundidade esperado e a bibliografia associada, se especificada. Identifica os subtemas e conceitos que compõem este tema específico. Não produz output nesta fase — é apenas análise.

**Fase 2 — Geração do material:**  
Com base no mapeamento da Fase 1, gera o conteúdo de aulas **apenas sobre o tema indicado**. Produz:

- Um slide de introdução ao tema com os objetivos de aprendizagem
- Slides de desenvolvimento com os conceitos fundamentais explicados de forma clara e progressiva
- Exemplos práticos e, onde aplicável, código ou fórmulas relevantes
- Um slide de síntese com os pontos-chave do tema

**Critérios de qualidade:**

- O conteúdo de cada slide deve ser conciso mas suficientemente explicado — não listas de tópicos vazios
- A progressão entre slides deve ser lógica — cada slide deve fazer sentido depois do anterior
- Exemplos e código devem ser concretos e diretamente relacionados com o tema, não genéricos
- Não desenvolvas outros temas da FUC além do indicado, mesmo que estejam relacionados
- Se o tema indicado não estiver claramente definido na FUC, sinaliza com _[TEMA AMBÍGUO — REQUER CLARIFICAÇÃO DO DOCENTE]_
- Se a profundidade esperada do tema não for clara na FUC, assume nível introdutório e sinaliza com _[PROFUNDIDADE ASSUMIDA]_
  