#Função que identifica os símbolos mais frequente e menos frequente de um ficheiro passado como parâmetro, 
#indicando a frequência de ocorrência desses dois símbolos.
import collections

<<<<<<< HEAD
def SymbolFrequency(FileName):
    #o open abre o arquivo em modo leitura, 'r', e está associado à variável f. O with garante q o arquivo 
    #é devidamente fechado prevenindo corrupção de dados.
    with open(FileName, 'r') as f:
=======
def SymbolFrequency(nome_arquivo):
    #o open abre o arquivo para leitura
    with open(nome_arquivo) as f:
>>>>>>> c8c2ef07be994ea4b65f89ed28398f446c0ac1cd
        # cria um contador de frequência para os símbolos no arquivo
        counter = collections.Counter(f.read())
        
        #o sorted ordena os símbolos pela frequência(do menos ao mais frequente)
        symbols = sorted(counter.items(), key=lambda x: x[1])
        
        #retorna o símbolo menos frequente e sua frequência
        leastFrequency = symbols[0]
        #retorna o símbolo mais frequente e sua frequência
        mostFrequency = symbols[-1]
        
        return (leastFrequency, mostFrequency)
