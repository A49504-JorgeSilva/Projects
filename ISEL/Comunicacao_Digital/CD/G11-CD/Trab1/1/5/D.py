# Função que apresenta o histograma de um ficheiro, o valor da informação própria de cada símbolo e a entropia do ficheiro.
import collections
import os
from io import StringIO
from unittest.mock import patch

import matplotlib.pyplot as plt
import math


def checkSymbol(key):
    if key == "\n":
        return r"\n"
    else:
        return key


def FileInformation(FileName):
    with open(FileName, 'r') as f:
        counter = collections.Counter(f.read())

    # HISTOGRAMA

    simbolos = list(map(lambda k: checkSymbol(k), counter.keys()))

    # plt.bar cria um histograma e ao usarmos os valores dentro do counter cria um histograma de frequências
    plt.bar(simbolos, list(counter.values()))
    # plt.xlabel trata do eixo dos x, neste caso os símbolos
    plt.xlabel('Símbolos')
    # plt.ylabel trata do eixo dos y, neste caso a frequência
    plt.ylabel('Frequência')
    # plt.title trata do título do histograma, neste caso Frequência de Símbolos
    plt.title('Frequência de Símbolos')
    # plt.show dá display do histograma
    plt.show()

    # VALOR DE INFORMAÇÃO PRÓPRIA

    # faz a soma dos valores todos presentes em counter
    total = sum(counter.values())
    # probabilidade de cada símbolo é calculada dividindo a sua frequencia com o total de símbolos
    probabilities = [count / total for count in counter.values()]
    # informação própria de cada símbolo é calculada segundo a fórmula I(x)=log(1/P(x)), encontra-se em bits
    selfInfo = list(map(lambda p: -math.log2(p), probabilities))

    print("Informação Própria:")
    j = 0
    for i in counter.keys():
        sI = selfInfo.__getitem__(j)
        g = str(i)
        if (i == "\n"):
            print("O simbolo ENTER tem I(X)= %.4f" % sI)
        else:
            if (i == " "):
                print("O simbolo ESPAÇO tem I(X)= %.4f" % sI)
            else:
                print("O simbolo " + g + " tem I(X)= %.4f" % sI)
        j = j + 1
    j = 0
    print("Probabilidades:")
    for i in counter.keys():
        sP = probabilities.__getitem__(j) * 100
        if (i == "\n"):
            print("O simbolo ENTER tem Probabilidade = %.4f" % sP)
        else:
            print("O simbolo " + i + " tem Probabilidade = %.4f" % sP)
        j = j + 1

    # ENTROPIA

    # calcula a entropia a partir da formula da entropia --> -sum ( P(x)log(P(x))), encontra-se em bits/simbolo,
    # quanto maior o valor maior a sua incerteza
    entropy = 0
    for i in range(len(probabilities)):
        entropy += probabilities.__getitem__(i) * selfInfo.__getitem__(i)

    print("A entropia do ficheiro é: %.4f" % entropy)  #:.2f --> 2 casas decimais


def test_FileInformation():
    with open('test_file.txt', 'w') as f:
        f.write('aaaabbbcc\n')
    with patch('sys.stdout', new=StringIO()) as fakeOutput:
        FileInformation('test_file.txt')
        output = fakeOutput.getvalue()
        assert 'O simbolo a tem I(X)= 1.3219' in output
        assert 'O simbolo b tem I(X)= 1.7370' in output
        assert 'O simbolo c tem I(X)= 2.3219' in output
        assert 'O simbolo ENTER tem I(X)= 3.3219' in output
        assert 'O simbolo a tem Probabilidade = 40.0000' in output
        assert 'O simbolo b tem Probabilidade = 30.000' in output
        assert 'O simbolo c tem Probabilidade = 20.0000' in output
        assert 'O simbolo ENTER tem Probabilidade = 10.0000' in output
    with patch('sys.stdout', new=StringIO()) as fakeOutput:
        FileInformation('test_file.txt')
        output = fakeOutput.getvalue()
        assert 'A entropia do ficheiro é: 1.8464' in output
    os.remove('test_file.txt')
    print("\nAll tests passed!\n")


FileInformation(r"C:\Users\ricar\Documents\Universidade\CD\CD42D-G11\ListaPalavrasEN.txt")
test_FileInformation()
