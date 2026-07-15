# Projetos — Licenciatura em Engenharia Informática e de Computadores (ISEL)

Repositório com os trabalhos práticos e projetos académicos da licenciatura, organizados por disciplina. Cada pasta tem o seu próprio `README.md` com a descrição do projeto, stack tecnológica e, quando aplicável, instruções para correr.

## Como navegar

| Disciplina | Projeto(s) | Destaque |
|---|---|---|
| [Projeto e Seminário](ISEL/Projeto_e_Seminário) | [Projeto Final de Curso](ISEL/Projeto_e_Seminário/Projeto_Final_de_Curso) | **Projeto de tese**: estudo sobre geração de material pedagógico com LLMs (Claude/DeepSeek/LeChat) + aplicação web Kotlin/Ktor que operacionaliza o processo. |
| [Laboratório de Software](ISEL/Laboratório_de_Software) | [Project](ISEL/Laboratório_de_Software/Project) | API REST em Kotlin (http4k + JDBC/PostgreSQL) para gestão de arrendamento de casas e reservas, com autenticação por token. |
| [Linguagens e Ambientes de Execução](ISEL/Linguagens_Ambientes_Execucao) | [Project1](ISEL/Linguagens_Ambientes_Execucao/Project1), [Project2](ISEL/Linguagens_Ambientes_Execucao/Project2), [Project3](ISEL/Linguagens_Ambientes_Execucao/Project3) | Reflection e geração de bytecode em runtime (JVM internals): router HTTP automático, parser YAML/JSON e um Repository JDBC automático, com benchmarks JMH. |
| [Programação Concorrente](ISEL/Programacao_Concorrente) | [Project](ISEL/Programacao_Concorrente/Project) | Primitivas de concorrência implementadas do zero em Kotlin: locks/condition variables, algoritmos lock-free (CAS) e um servidor pub/sub assíncrono com coroutines. |
| [Sistemas de Informação](ISEL/Sistemas_Informacao) | [Project](ISEL/Sistemas_Informacao/Project) | Sistema de partilha de trotinetes elétricas (CITES) em Java + Maven + JPA/PostgreSQL. |
| [Introdução aos Sistemas de Informação](ISEL/Introducao_Sistemas_Informacao) | [Project1](ISEL/Introducao_Sistemas_Informacao/Project1), [Project2](ISEL/Introducao_Sistemas_Informacao/Project2), [Project3](ISEL/Introducao_Sistemas_Informacao/Project3) | Três sistemas de partilha de mobilidade (veículos, bicicletas, trotinetes) modelados de raiz: BD relacional (SQL) + aplicação Java/JDBC. |
| [Técnicas de Desenvolvimento de Software](ISEL/Tecnicas_Desenvolvimento_Software) | [TDS](ISEL/Tecnicas_Desenvolvimento_Software/TDS) | Jogo de Go em Kotlin, com versão terminal e versão GUI (Compose Desktop + MongoDB). |
| [Comunicação Digital](ISEL/Comunicacao_Digital) | [CD/G11-CD](ISEL/Comunicacao_Digital/CD/G11-CD) | Teoria da informação, cifragem e codificação de canal (Hamming, deteção de erros), com comunicação série real via Arduino. |
| [Laboratório de Informática de Computadores](ISEL/Laboratorio_Informatica_Computadores) | [Project](ISEL/Laboratorio_Informatica_Computadores/Project) | Sistema de controlo de acesso a uma porta, em VHDL, sintetizado para FPGA (Quartus + DE10-Lite). |
| [Algoritmos e Estruturas de Dados](ISEL/Algoritmo_e_Estrutura_de_Dados) | [Séries](ISEL/Algoritmo_e_Estrutura_de_Dados/Séries) | Estruturas de dados clássicas implementadas do zero (hash table, listas ligadas, árvores, grafos) com aplicações práticas sobre datasets reais. |

## Ordem sugerida de leitura

Para uma visão rápida do nível técnico, os projetos mais representativos são: **Projeto Final de Curso** (tese), **Laboratório de Software** (API REST completa), e **Linguagens e Ambientes de Execução** (reflection/bytecode + benchmarks). Os restantes são trabalhos de disciplina mais curtos e focados num tema específico.
