# Jogo de Go

Implementação do jogo de tabuleiro **Go** em Kotlin, com duas interfaces sobre o mesmo modelo de domínio: uma versão **terminal** e uma versão **GUI desktop**.

Autores: Gonçalo Pinto, Jorge Silva (Turma 31D, Grupo 07).

## Domínio comum

Tabuleiro 9x9 representado por um `sealed class Board` com estados `BoardRun`/`BoardFinish`/`BoardPass`, modelo imutável (`Board`, `Game`, `Group`, `Position`, `Stone`). Regras implementadas:

- **Captura de grupos por liberdades** — deteção de grupos de pedras adjacentes por *flood-fill* e remoção quando ficam sem liberdades.
- **Fim de jogo por duplo passe** consecutivo.
- **Pontuação por área**, com komi ajustado ao tamanho do tabuleiro.

## [GoTerminal](GoTerminal) — versão CLI

Comandos de consola (`NEW`, `PLAY <pos>`, `PASS`, `SAVE <nome>`, `LOAD <nome>`, `EXIT`). Persistência em ficheiro de texto (`TextFileStorage`), com jogos gravados na pasta [`games/`](GoTerminal/games). Testes unitários ao tabuleiro, ao jogo e à persistência.

## [GoCompose](GoCompose) — versão GUI

Interface gráfica desktop em **JetBrains Compose for Desktop** (padrão MVVM: `AppViewModel` + views declarativas para tabuleiro, pontuação e barra de estado). Persistência em **MongoDB** em vez de ficheiros. Gera distribuições nativas (Dmg/Msi/Deb) via `compose.desktop.application`.

## Stack

Kotlin/JVM, Gradle Kotlin DSL, Compose for Desktop, MongoDB driver (Kotlin sync), JUnit5.
