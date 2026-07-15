# Sistema de Controlo de Acesso (FPGA)

Projeto final da disciplina de Laboratório de Informática de Computadores: um **sistema de controlo de acesso a uma porta**, desenhado do zero em **VHDL** a partir de blocos de lógica digital construídos ao longo do semestre (somadores, contadores, registos, RAM), sintetizado para a placa **DE10-Lite** (FPGA Intel MAX10 `10M50DAF484C6GES`) com **Quartus Prime** e verificado módulo a módulo em **ModelSim**.

## Funcionamento

Um utilizador introduz um código num teclado matricial 4x4; o código é validado por uma máquina de estados que controla a abertura/fecho de uma porta, com deteção de presença por sensor e feedback em displays de 7 segmentos.

```
KEYPAD ──▶ Keyboard_Reader ──▶ UsbPort (barramento de 8 bits, handshake) ──┬──▶ SDC  ──▶ DoorController ──▶ OnOff / Dout (porta)
                                                                            └──▶ SLCDC ──▶ WrL / Dout (canal auxiliar)
```

### 1. Leitura do teclado — [`Keyboard_Reader.vhd`](Keyboard_Reader.vhd)

Pipeline de 3 estágios com desacoplamento por *handshake*:

- **`Key_Decode`** — faz o *scan* das linhas/colunas do keypad e descodifica a tecla premida num código de 4 bits, sinalizando `Kval`.
- **`Ring_Buffer`** — FIFO circular que absorve teclas premidas em sequência (protocolo `DAV`/`DAC` com quem escreve, `CTS`/`Wreg` com quem lê), para a leitura do teclado não ficar bloqueada à espera do consumidor.
- **`Output_Buffer`** — entrega o código ao barramento externo com um handshake `Dval`/`ACK`, libertando-se (`OBfree`) assim que o consumidor confirma a leitura.

### 2. Barramento partilhado — [`UsbPort.vhd`](UsbPort.vhd)

Um registo de I/O genérico de 8 bits liga o teclado aos dois controladores série, com mapeamento fixo de bits (ver [`AccessControlSystem.vhd`](AccessControlSystem.vhd)): `SDX`=bit0, `SCLK`=bit1, `NOT_SS` de cada controlador em bits 2/3, `Dval` do teclado em bit4, `busy` do SDC em bit7.

### 3. Controladores série — [`SDC.vhd`](SDC.vhd) / [`SLCDC.vhd`](SLCDC.vhd)

Ambos seguem o mesmo padrão: um `SerialReceiver` (protocolo síncrono `SS`/`SCLK`/`SDX`, com sinal `busy` enquanto recebe) entrega os dados recebidos a um consumidor.

- **SDC (Serial Data Controller)** entrega o código ao `DoorController` e devolve-lhe também os sensores `Sclose`/`Sopen`/`Psensor`, controlando o acionamento (`OnOff`) da porta.
- **SLCDC (Serial Link Control Data Controller)** é um segundo canal série independente, usado para escrever o resultado (`WrL`/`Dout`) num periférico externo (ex.: atuador/emulador da porta), sem interferir no canal de controlo do SDC.

### 4. Máquina de estados da porta — [`DoorController.vhd`](DoorController.vhd)

5 estados, transitando por código introduzido (`Dval`/`Din(0)`) e pelos sensores físicos:

| Estado | Significado | Transita para |
|---|---|---|
| `SYSTEM_OFF` | Porta fechada, à espera de código | `OPENING` (código válido) / `WAITING` (código inválido) |
| `WAITING` | Código inválido — a aguardar que o sensor de presença liberte | `CLOSING` quando `Psensor = 0` |
| `OPENING` | A abrir | `COMPLETED` se `Sopen = 1` e código válido; `SYSTEM_OFF` se `Sopen = 1` e código inválido |
| `CLOSING` | A fechar | volta a `OPENING` se deteta presença (`Psensor = 1`); `COMPLETED` quando `Sclose = 1` |
| `COMPLETED` | Ciclo terminado | `SYSTEM_OFF` quando o código deixa de estar ativo (`Dval = 0`) |

`OnOff` fica ativo durante `OPENING`/`CLOSING` (aciona o motor), `OpenClose` indica o sentido, `done` sinaliza `COMPLETED` de volta ao `SerialReceiver`.

### 5. Feedback visual

6 displays de 7 segmentos (`HEX0`-`HEX5`) mostram o estado do sistema; [`door_emulation_seg.vhd`](door_emulation_seg.vhd) e [`door_mecanism.vhd`](door_mecanism.vhd) implementam um emulador do mecanismo físico da porta (posição/velocidade) usado nos testes, antes de haver hardware real ligado.

## Blocos de suporte reutilizados

Construídos e testados em exercícios anteriores da disciplina, e reutilizados aqui como building blocks: somadores (`Full_adder_4bits`, `adder3bit/4bit`, `adder_rc`), contadores (`Counter_3bit/4bit`, `COUNTER_INC_DEC`), descodificadores (`decoder2_3`, `decoder2_4`), multiplexadores (`MUX2_1`, `mux4_1`), registos e *shift registers* (`Registor`, `Ring_Buffer`, `ShiftRegister_SR/lr`), memória (`RAM.vhd`) e divisor de relógio (`Clk_div.vhd`).

## Verificação

Praticamente todos os módulos têm o respetivo *testbench* (`*_tb.vhd` — ex.: `DoorController_tb`, `SDC_tb`, `SLCDC_tb`, `Keyboard_Reader_tb`, `Key_decode_tb`, `Ring_Buffer_tb`), simulados individualmente em ModelSim antes da integração no sistema completo.

## Stack / ferramentas

VHDL, Quartus Prime (síntese, atribuição de pinos via `.qsf`, um por módulo), ModelSim (simulação com scripts `.do`), placa Intel DE10-Lite (FPGA MAX10).

## Como compilar / simular

1. Abrir `Project.qpf` no Quartus Prime, compilar (`Processing > Start Compilation`) e programar a DE10-Lite com o `.sof` gerado.
2. Para simular um módulo isolado, abrir o `.do` correspondente em ModelSim (ex.: `simulation/modelsim/full_adder.do`) ou correr o testbench diretamente.

## Nota sobre o repositório

As pastas `db/`, `incremental_db/`, `output_files/` e `simulation/modelsim/rtl_work` contêm artefactos de compilação gerados automaticamente pelo Quartus/ModelSim (bases de dados internas, bitstreams, relatórios de síntese) — não são código fonte e podem ser regenerados a qualquer momento a partir dos ficheiros `.vhd`/`.qsf`.
