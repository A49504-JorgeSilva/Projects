#Função que identifica os símbolos mais frequente e menos frequente de um ficheiro passado como parâmetro, 
#indicando a frequência de ocorrência desses dois símbolos.
import collections

def SymbolFrequency(nome_arquivo):
    #o open abre o arquivo para leitura
    with open(nome_arquivo, 'r') as f:
        # cria um contador de frequência para os símbolos no arquivo
        counter = collections.Counter(f.read())
        
        #o sorted ordena os símbolos pela frequência(do menos ao mais frequente)
        symbols = sorted(counter.items(), key=lambda x: x[1])
        
        #retorna o símbolo menos frequente e sua frequência
        leastFrequency = symbols[0]
        #retorna o símbolo mais frequente e sua frequência
        mostFrequency = symbols[-1]
        
        return (leastFrequency, mostFrequency)
