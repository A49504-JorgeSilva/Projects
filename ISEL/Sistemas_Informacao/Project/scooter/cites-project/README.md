# CITES — Sistema de Partilha de Trotinetes Elétricas

Projeto da disciplina de Sistemas de Informação: gestão de uma rede de partilha de trotinetes elétricas ("scooter sharing"), com estações, docas, cartões de cliente e viagens.

## Modelo de domínio

- `Client` — cliente registado, com cartão(ões) associado(s).
- `TypeOfCard` — tarifário (residente/turista), com nº de dias e preço.
- `Card` — crédito monetário de um cliente associado a um tipo de cartão.
- `Station` / `Dock` — localização geográfica e vagas de estacionamento (livre/ocupada), cada uma com a sua trotinete.
- `ScooterModel` / `Scooter` — modelo (peso, velocidade máxima, bateria) e unidade concreta.
- `Travel` — viagem de um cliente entre estações, com avaliação.
- `ServiceCost` — custo de desbloqueio + custo por minuto de uso.

## Casos de uso implementados

App de consola (`isel.sisinf.ui`) com menu numerado: registar cliente com cartão e crédito inicial, listar clientes e docas/estações, iniciar viagem (verifica crédito, procura trotinete disponível na estação, debita custo de desbloqueio) e terminar viagem (calcula duração, debita custo proporcional ao tempo, liberta a doca).

## Stack

Java + Maven multi-módulo, **JPA/Jakarta Persistence** (provider EclipseLink) sobre **PostgreSQL**. Esquema SQL em [`cites-SQL`](../cites-SQL) (`create-model.sql`, `insert-data.sql`).

> Nota de arquitetura: apesar da separação em 3 módulos Maven, os módulos `model` e `dal` são o esqueleto fornecido pelo enunciado (stubs "for demonstration purpose only"); toda a lógica de negócio, as entidades JPA e a gestão de transações estão implementadas no módulo `app` (pacote `isel.sisinf.ui`).

---

# Multi-Module Project with Maven

This directory contains a multi-module project managed by [Apache Maven](https://maven.apache.org). It consists of three modules: `dal`, `model`, and `app`.

## Modules

### dal

The `dal` (Data Access Layer) module must contains the implementation of data access logic, using [Jackarta Persistence](https://jakarta.ee/specifications/persistence/3.1/).

### model

The `model` module defines the data model and domain objects used across the project. It should include entity classes, DTOs (Data Transfer Objects), and other model-related components. Both `app` and `dal` modules will use those definitions.

### app

The `app` module is the main application module that brings together the functionality provided by the `dal` and `model` modules. It must contains only the business logic, service layer, and user interface (UI) components. Do note the **main components of the UI are already implemented and must not be changed!**

## Usage

To build the project, run the following command in the root directory:

```bash
mvn clean package
```

This command will compile, test, and package all the modules.
To additionally run the application, execute:

```bash
mvn clean verify
```

This command will also start the application.

To skip the test (once properly tested) use:
```bash
mvn clean verify -Dmaven.test.skip
```

For more information about how to use maven see the [documentation](https://maven.apache.org/guides/index.html).

## License

This project is licensed under the [MIT License](https://opensource.org/license/mit).

[Nuno Datia](https://www.isel.pt/docentes/nuno-miguel-soares-datia) @ [ISEL](https://www.isel.pt)