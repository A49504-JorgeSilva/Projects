# Algoritmos e Estruturas de Dados (AED)

Exercícios práticos ("séries") da disciplina de Algoritmos e Estruturas de Dados, em **Kotlin**. Cada série implementa estruturas de dados clássicas do zero (sem usar as da standard library) e resolve um pequeno problema aplicado sobre elas, com testes unitários (JUnit5/kotlin-test).

## [Série 1](Séries/Serie1) — Arrays e pesquisa/ordenação

- `squaresSorted` — ordena os quadrados de um array já ordenado em O(n) com a técnica *two pointers*.
- `countInRange` — pesquisa binária (`upperBound`/`lowerBound`) para contar elementos num intervalo.
- `counter` / `countInverses` — *sliding window* sobre arrays.
- **Aplicação**: `GreatestNrOccurrencies1` lê ficheiros de texto, conta a frequência de palavras com uma heap (`PriorityQueue`) e escreve as top-K palavras mais frequentes num ficheiro, com medição de tempo de execução.

## [Série 2](Séries/serie2_21d-l21d_grupo9) — Listas ligadas e tabelas de hash

- Lista duplamente ligada circular implementada manualmente, com **quicksort in-place sobre a lista**, interseção ordenada de duas listas e obtenção do k-ésimo maior elemento.
- **`HashMap` própria**: tabela de hash com chaining (array de buckets + listas ligadas), resize automático quando o load factor ultrapassa 0.75, e iterador custom — implementação completa do zero de `MutableMap`.
- **Aplicação**: codificação de ficheiros de texto por frequência de palavras (conta ocorrências, ordena e substitui cada palavra por um código).

## [Série 3](Séries/serie3_21d-l21d_grupo9) — Árvores e grafos

- Árvore binária de pesquisa: validação de propriedade BST (`isBST`), verificação de árvore completa e contagem de múltiplos de k.
- Grafo dirigido e pesado implementado com um contrato genérico (`Graph<I,D>`).
- **Aplicação `TouristPath`**: calcula o caminho mais curto entre dois nós num grafo pesado carregado a partir de um ficheiro em formato DIMACS, testado sobre um dataset real da rede rodoviária de Nova Iorque (`USA-road-d.NY.gr`), com benchmark de desempenho incluído.

## Stack

Kotlin/JVM, Gradle (Kotlin DSL), JUnit5.

## Nota

A pasta `Séries/Archive` contém uma cópia idêntica dos ficheiros de dados da Série 1 (resíduo de uma extração de `.zip` no macOS, incluindo uma pasta `__MACOSX`) e pode ser ignorada.
