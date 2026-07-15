# Programação Concorrente (PC)

Três séries de exercícios da disciplina de Programação Concorrente, em **Kotlin**: implementação do zero de primitivas de sincronização, algoritmos *lock-free* e um servidor assíncrono, todas com testes unitários de concorrência/stress.

Autores: Jorge Silva, Tiago Neves, Daniel Antunes.

| Série | Técnica | O que foi implementado |
|---|---|---|
| [Set 1](src/main/kotlin/pt/isel/pc/problemsets/set1) | Locks + condition variables | `BlockingMessageQueue` (fila bloqueante com capacidade limitada e timeouts), `CountDownLatch`, `Exchanger` (troca de valores entre threads) e um `ThreadPoolExecutor` próprio (criação dinâmica de threads, fila de trabalho, `keepAliveTime`) — reimplementações das primitivas equivalentes do JDK. |
| [Set 2](src/main/kotlin/pt/isel/pc/problemsets/set2) | Lock-free (CAS) | `SafeResourceManager`/`SafeSuccession` (contadores e iteração segura com `AtomicInteger`/`compareAndSet`, sem locks) e um combinador `any` para `CompletableFuture` (devolve o primeiro sucesso entre vários futures). |
| [Set 3](src/main/kotlin/pt/isel/pc/problemsets/set3) | Coroutines + NIO2 | Extensões `suspend` que fazem a ponte entre `AsynchronousSocketChannel`/`CompletionHandler` (NIO2) e corrotinas Kotlin, uma fila assíncrona sem bloqueio de threads (`CancellableContinuation`), e um **servidor de mensagens pub/sub completo** (tópicos, subscritores, protocolo próprio) que corre sobre `Dispatchers.Default`/`Dispatchers.IO`. |

## Stack

Kotlin 1.9 / JVM 21, Gradle Kotlin DSL, `kotlinx-coroutines-core`, JUnit5/kotlin-test, ktlint.

## Notas

Enunciados originais em [`docs/`](docs). Observações de implementação da Série 3: uso de `Dispatchers.Default` para operações comuns e `Dispatchers.IO` para leitura/escrita, com buffer para pedidos de cliente; o envio de mensagens a subscritores de um tópico ficou com uma limitação conhecida (bloqueio de thread) por resolver.
