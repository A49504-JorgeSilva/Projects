# Linguagens e Ambientes de Execução (LAE)

Trabalhos práticos da disciplina de Linguagens e Ambientes de Execução, focada em mecanismos de execução da JVM: **Reflection API** (Java e Kotlin) e **geração de bytecode em runtime**, com avaliação de desempenho via **JMH**. O padrão comum aos 4 trabalhos é implementar o mesmo problema de duas formas — reflection (mais simples, mais lenta) vs. geração dinâmica de código (mais complexa, mais rápida) — e medir a diferença.

- **[Project1 — autorouter](Project1/Project)** — geração automática de handlers HTTP a partir de um controller anotado.
- **[Project2 — yamlify](Project2/Project)** — parser YAML → objeto Kotlin *(exercício incompleto, ver README próprio)*.
- **[Project3/Project1 — jsonoy](Project3/Project1)** — encoder JSON via reflection.
- **[Project3/Project2 — jdbcRepo](Project3/Project2/jdbcrepo-42d-42d-g03-main)** — implementação automática do padrão *Repository* sobre JDBC (o mais completo dos 4, com 3 fases: reflection, geração de bytecode via Class-File API do JDK 22 e queries lazy).

## Stack comum

Kotlin/Java, Gradle, kotlin-reflect, [Cojen Maker](https://github.com/cojen/Maker) / Class-File API para geração de bytecode, JMH para microbenchmarks, JUnit5.
