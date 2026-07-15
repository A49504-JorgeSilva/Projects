# Sistema de Controlo de Acesso (FPGA)

Projeto final da disciplina de Laboratório de Informática de Computadores: um **sistema de controlo de acesso a uma porta** implementado em **VHDL** para a placa **DE10-Lite** (FPGA Intel MAX 10, `10M50DAF484C6GES`), sintetizado com **Quartus Prime** e simulado em **ModelSim**.

## Funcionamento

O utilizador introduz um código num teclado matricial; o sistema valida-o através de um protocolo de comunicação série interno e aciona a abertura/fecho controlada de uma porta, com deteção de presença por sensor e feedback em displays de 7 segmentos.

Módulo de topo: [`AccessControlSystem.vhd`](AccessControlSystem.vhd), que integra:

- **Teclado matricial** (`Keyboard_Reader.vhd`, `Key_scan.vhd`, `Key_decode.vhd`) — leitura e descodificação de um keypad 4x4.
- **Comunicação série interna** (`SDC.vhd`, `SLCDC.vhd`, `SerialReceiver.vhd`) — protocolo síncrono (SCLK/SDX/SS) entre módulos.
- **Controlo da porta** (`DoorController.vhd`) — máquina de estados finita com 5 estados (`SYSTEM_OFF`, `WAITING`, `OPENING`, `CLOSING`, `COMPLETED`), reagindo a sensores de abertura/fecho/presença e a um interruptor de ativação.
- **Interface genérica de I/O** (`UsbPort.vhd`) e saída para 6 displays de 7 segmentos (`HEX0`-`HEX5`).
- **Blocos de lógica digital de suporte**, reutilizados e integrados a partir de exercícios anteriores da disciplina: somadores, contadores, descodificadores, multiplexadores, registos/shift registers e uma RAM.

Cada módulo tem o respetivo *testbench* (`*_tb.vhd`) para verificação em simulação.

## Stack / ferramentas

VHDL, Quartus Prime (síntese e atribuição de pinos via `.qsf`), ModelSim (simulação), placa Intel DE10-Lite (MAX 10).

## Nota sobre o repositório

As pastas `db/`, `incremental_db/`, `output_files/` e `simulation/modelsim/rtl_work` contêm artefactos de compilação gerados automaticamente pelo Quartus/ModelSim (bases de dados internas, bitstreams, relatórios de síntese) — não são código fonte e podem ser regenerados a qualquer momento a partir dos ficheiros `.vhd`/`.qsf`.
