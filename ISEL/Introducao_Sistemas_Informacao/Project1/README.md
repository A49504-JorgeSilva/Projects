# Sistema de Partilha de Veículos (2022/23)

Trabalho de grupo da disciplina de Introdução aos Sistemas de Informação (ISI), Turma 31D, Grupo 25. Modela e implementa um sistema de gestão de uma frota de veículos partilhados entre **proprietários**, **condutores habilitados** e **clientes**.

## Modelo de dados

- `PESSOA` — entidade base com discriminador para Proprietário / Condutor / Cliente.
- `VEICULO` — com tipo e cor; pode ser desativado (arquivado com o total de km percorridos).
- `CONDHABILITADO` — condutores autorizados a conduzir cada veículo.
- `PERIODOACTIVO` — período em que um par veículo/condutor está em serviço, com coordenadas GPS.
- `VIAGEM` / `CLIENTEVIAGEM` — viagens com hora de início/fim, coordenadas, valor e classificação, partilhadas entre vários clientes ("boleia partilhada").

Ficheiros: [modelação (TP1)](LEIC31DGrupo25MP), [SQL (TP2)](TP2ISI-2223-Grupo-mp-25/TP2ISI-2223-Grupo-mp-25), [aplicação Java (TP3)](TP3ISI-2223-Grupo-mp-25/LEIC31DGrupo25MP_T3).

## Aplicação (TP3)

App de consola em Java com JDBC sobre PostgreSQL, com um menu de operações:

1. Inserir novo condutor (com validação do número de carta de condução).
2. Desativar um veículo (calcula o total de km percorridos com a fórmula de distância de Haversine sobre as coordenadas das viagens).
3. Estatísticas de um veículo (tempo total de viagens, km, custo).
4. Condutor com mais viagens num ano.
5. Condutores que nunca fizeram uma viagem.
6. Número de viagens por ano dos veículos de um proprietário.
7. Condutor mais bem pago num ano.

Ao arrancar, a aplicação aplica automaticamente um conjunto de regras de negócio/integridade (`RT.java`): remove proprietários com mais de 20 veículos, remove certos condutores e arquiva viagens antigas.

## Stack

Java + JDBC + PostgreSQL. Compilação via IntelliJ/`javac`.

> Nota técnica: algumas queries constroem SQL por concatenação de strings em vez de `PreparedStatement` parametrizado — um ponto de melhoria óbvio numa revisão futura (risco de SQL injection).
