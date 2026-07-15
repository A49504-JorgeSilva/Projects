# Comunicação Digital — Turma 42D, Grupo 11

Dois trabalhos práticos sobre teoria da informação e codificação de canal, em **Python**, **C** e **Arduino**.

Autores: Ricardo Rovisco, João Mota, Jorge Silva.

## [Trabalho 1](Trab1) — Teoria da informação

- **Manipulação de bits em C** ([`1/4`](Trab1/1/4)): contagem de bits a 1/0 num inteiro, procura do símbolo mais frequente num ficheiro, impressão da representação binária, e geração do "negativo" bit a bit de um ficheiro.
- **Entropia e frequência de símbolos em Python** ([`1/5`](Trab1/1/5)): cálculo do máximo divisor comum, contagem do símbolo mais/menos frequente de um ficheiro (`collections.Counter`), histograma de frequências (`matplotlib`) e cálculo da informação própria e da entropia de Shannon de um ficheiro.
- **Cifra de Vernam** ([`Trab1/4`](Trab1/4)): cifra/decifra por XOR (stream cipher) sobre ficheiros.
- **Canal binário simétrico e interleaving** ([`Trab1/5`](Trab1/5)): simulação de ruído num canal digital (BSC) com e sem entrelaçamento (interleaving) de dados.
- [`TestFilesCD`](Trab1/TestFilesCD): ficheiros de teste padrão (texto, imagem, código-fonte) usados nos exercícios acima.

## [Trabalho 2](Trab2) — Codificação de canal

- **Deteção/correção de erros** ([`Trab2/1`](Trab2/1)): simulação de um canal binário simétrico (BSC) com **código de Hamming(7,4)** (correção de 1 bit) e **código de repetição**, com e sem interleaving, testados sobre vários ficheiros de input e um texto de referência ("Alice in Wonderland").
- **Deteção de erros em rajada (burst) e comunicação série com hardware real** ([`Trab2/2`](Trab2/2)): scripts de deteção de erros isolados e em rajada, e um sketch **Arduino** (`TrabCD.ino`) que gera uma progressão e a envia por porta série, lida do lado do PC em Python via `pyserial`.

## Stack

Python 3 (`collections`, `matplotlib`, `pyserial`), C, Arduino (C++).
