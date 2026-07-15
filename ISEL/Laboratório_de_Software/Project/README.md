# Houses API — Sistema de Gestão de Arrendamento de Casas

Projeto da disciplina de Laboratório de Software: uma **API REST** em Kotlin para gestão de casas para arrendamento, localizações hierárquicas e reservas, com autenticação por token.

## Domínio

- **User** — nome, email único, password (hash BCrypt) e token de autenticação (UUID).
- **Location** — hierarquia de 5 níveis auto-referenciada: `COUNTRY > REGION > CITY > MUNICIPALITY > LOCALITY`.
- **House** — título, área, preço por noite, dono; só pode ser criada numa `Location` do tipo `LOCALITY`.
- **Booking** — reserva de uma casa por um utilizador num intervalo `[início, fim)`; não permite sobreposição de datas para a mesma casa nem reservas no passado.

## Arquitetura

```
Handler (webapi/)  →  Service (services/)  →  Repository (data/)  →  PostgreSQL (JDBC) | memória (testes)
```

- **Handlers** (`webapi/*Handler.kt`) — parsing do token Bearer, (de)serialização JSON (`kotlinx.serialization`) e mapeamento de exceções de domínio para códigos HTTP (404 não encontrado, 400/409 conflito ou input inválido, 422 hierarquia inválida, 401 não autenticado).
- **Services** — regras de negócio (validação de datas, hierarquia de localizações, paginação, hashing de password).
- **Repositories** — interface comum com duas implementações: `postgres/` (JDBC puro, `PreparedStatement`) e `mem/` (usada nos testes).
- **Server** (`HousesServer.kt`) — composition root: monta o `DataSource`, instancia repositórios/serviços/handlers e arranca o servidor (Jetty, porta 8080).

## Principais endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/users` | Regista utilizador |
| POST | `/login` | Autentica por email/password |
| POST | `/houses` | Cria casa (requer token) |
| GET | `/houses` | Lista casas, com paginação e pesquisa por título/localização |
| POST | `/bookings` | Cria reserva (valida datas e sobreposição) |
| GET | `/bookings/available` | Casas disponíveis num período |
| POST | `/locations` | Cria localização (valida hierarquia) |
| GET | `/locations/{id}/path` | Caminho na hierarquia até ao país |

Especificação completa em [`docs/openapi.yaml`](docs/openapi.yaml); exemplos de pedidos em [`docs/DemoHTTPrequests.http`](docs/DemoHTTPrequests.http).

## Stack

Kotlin, **http4k** (routing + servidor Jetty), **kotlinx.serialization**, **PostgreSQL** via JDBC puro (sem ORM), **BCrypt** para passwords, testes com `kotlin.test`/JUnit (serviços contra repositório em memória, handlers via request/response HTTP simulado).

## Documentação

[`docs/ProjectReport.md`](docs/ProjectReport.md) é o relatório completo do projeto (modelo entidade-associação, modelo físico, fluxo de pedidos, gestão de erros, testes e avaliação crítica) — cobre também uma SPA e deployment (Docker/Render) de uma fase posterior do trabalho que não está incluída neste snapshot, que contém apenas o backend.
