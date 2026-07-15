# Sistema de Aluguer de Bicicletas (2023/24)

Trabalho de grupo da disciplina de Introdução aos Sistemas de Informação (ISI), Grupo MP19. Modela e implementa uma rede de lojas de aluguer de bicicletas clássicas e elétricas, com dispositivos GPS e reservas de clientes.

## Modelo de dados

- `BICICLETA` — com subtipos `CLASSICA` / `ELETRICA` (discriminador).
- `DISPOSITIVO` — GPS associado a cada bicicleta (bateria, latitude/longitude).
- `LOJA` — com gerente e telefone(s) de contacto.
- `RESERVA` / `CLIENTERESERVA` — reserva de uma bicicleta numa loja por um período, com valor associado.

Ficheiros: [modelação (TP1)](TP12324-MP19), [SQL (TP2)](TP22324-MP19), [aplicação Java (TP3)](TP32324-MP19/Trab3/TP3).

## Aplicação (TP3)

App de consola em Java com JDBC sobre PostgreSQL, com um menu de operações:

1. Registar uma nova bicicleta (clássica ou elétrica) com o respetivo dispositivo GPS.
2. Atualizar o estado de uma bicicleta (ex.: colocar em manutenção).
3. Métricas médias (autonomia, velocidade, bateria) das bicicletas elétricas por marca.
4. Clientes com mais reservas num ano.
5. Dispositivos GPS de bicicletas em manutenção.
6. Lojas com mais de N reservas.
7. Gerentes de lojas com reservas efetuadas.

## Stack

Java + JDBC + PostgreSQL.

> Nota: o esquema SQL fornecido no enunciado desta edição continha erros de sintaxe (parênteses de `foreign key` mal fechados), típico de um exercício para os alunos corrigirem; por isso há pequenas divergências entre o schema e as queries da aplicação.
