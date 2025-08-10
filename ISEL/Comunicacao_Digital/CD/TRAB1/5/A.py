#Função que apresenta os primeiros N termos da progressão geométrica de primeiro termo u e razão r.
#Os valores de N, u e r são passados como parâmetro.

#function é defenida com def, e em vez de chavetas é :
def GeometricProgression(N,u,r): 
    for i in range(0,N):    
        #pow isto faz u(termo inicial) * r^i
        curr_Term = u * pow(r,i) 
        print(curr_Term, end=" ")

