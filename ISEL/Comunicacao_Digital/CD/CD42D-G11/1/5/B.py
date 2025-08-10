#Função que determina o máximo divisor comum entre dois números inteiros a e b, através do algoritmo de Euclides.

def mcd(a, b):
    c = a
    d = b
    if b>a:
        a, b = b,a
    while b!=0:
        r = a%b
        a, b = b , r
    print(f"O mcd({c},{d}) = {a}")

mcd(396,198)


