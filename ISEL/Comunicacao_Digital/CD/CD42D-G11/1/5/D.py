#Função que apresenta o histograma de um ficheiro, o valor da informação própria de cada símbolo e a entropia do ficheiro.
import collections
import matplotlib.pyplot as plt
import math

def FileInformation(FileName):
    with open(FileName, 'r') as f:
        counter = collections.Counter(f.read())
    
    #HISTOGRAMA

    #plt.bar cria um histograma e ao usarmos os valores dentro do counter cria um histograma de frequências
    plt.bar(list(counter.keys()), list(counter.values()))
    #plt.xlabel trata do eixo dos x, neste caso os símbolos
    plt.xlabel('Símbolos')
    #plt.ylabel trata do eixo dos y, neste caso a frequência
    plt.ylabel('Frequência')
    #plt.title trata do título do histograma, neste caso Frequência de Símbolos
    plt.title('Frequência de Símbolos')
    #plt.show dá display do histograma
    plt.show

    #VALOR DE INFORMAÇÃO PRÓPRIA

    #faz a soma dos valores todos presentes em counter
    total= sum(counter.values())
    #probabilidade de cada símbolo é calculada dividindo a sua frequencia com o total de símbolos
    probabilities= [count / total for count in counter.values()]
    #informação própria de cada símbolo é calculada segundo a fórmula I(x)=log(1/P(x)), encontra-se em bits
    selfinfo= list(map(lambda p: -math.log2(p),probabilities))

    #ENTROPIA

    #calcula a entropia a partir da formula da entropia --> -sum ( P(x)log(P(x))), encontra-se em bits/simbolo,
    #quanto maior o valor maior a sua incerteza
    entropy = sum(p * i for p, i in zip(probabilities, selfinfo))
    print(f"A entropia do ficheiro {FileName} é: {entropy:.2f}") #:.2f --> 2 casas decimais

FileInformation(r"C:\Users\jorge\Downloads\TestFilesCD\a.txt")