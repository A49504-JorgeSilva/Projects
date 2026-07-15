# Sistema de Partilha de Trotinetes Elétricas (2024/25)

Trabalho da disciplina de Introdução aos Sistemas de Informação (ISI). Modela e implementa um sistema de partilha de trotinetes elétricas com estações de estacionamento, cartões de cliente e reposição de trotinetes.

## Modelo de dados

- `PERSON` → `CLIENT` / `EMPLOYEE` (herança por chave partilhada).
- `CARD` / `TOPUP` — cartão de cliente com crédito e histórico de carregamentos, associado a um tarifário (`TYPEOF`: residente/turista).
- `STATION` / `DOCK` — estações com vagas ("docks") no estado livre/ocupada/em manutenção.
- `SCOOTERMODEL` / `SCOOTER` — modelo (peso, velocidade, bateria) e unidade concreta de trotinete.
- `TRAVEL` — viagem de um cliente entre duas estações, com avaliação de satisfação (1-5).
- `REPLACEMENTORDER` / `REPLACEMENT` — ordens de reposição de trotinetes entre estações, executadas por funcionários.

Ficheiros: [modelação (TP1)](TP1), [SQL (TP2)](TP2/SQL_Part2), [aplicação Java (TP2)](TP2/ISI-Project_Part2).

## Aplicação

App de consola em Java com JDBC sobre PostgreSQL (ligação configurável via `database.properties`, em vez de hardcoded como nas edições anteriores):

1. Registar novo utilizador com o respetivo cartão.
2. Listar ordens de reposição de uma estação num dado período.
3. Iniciar e terminar uma viagem.

As operações de gestão de docas, avaliações de satisfação e ocupação de estações estão identificadas como próximos passos no código.

> A pasta [`TP2/ISI-Project_Part2`](TP2/ISI-Project_Part2) inclui o esqueleto de classes fornecido pelo docente (README próprio com licença MIT) sobre o qual a lógica de acesso a dados foi implementada.

## Stack

Java + JDBC + PostgreSQL.
