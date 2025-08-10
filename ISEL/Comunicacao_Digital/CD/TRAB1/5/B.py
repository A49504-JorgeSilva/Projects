#Função que determina o máximo divisor comum entre dois números inteiros a e b, através do algoritmo de Euclides.

def mcd(a, b):
    #se não entender pra q serve o loop, pesquisar algoritmo de euclides
    while a % b != 0: 
        a = b
        b = a%b
    print(f"O mcd({a},{b}) = {b}")
