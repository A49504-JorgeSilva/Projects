# Testes:

## Claude

- Página 2: primeira "nota crítica" contém um erro.
    - Não é o HTTP que especifica a sintaxe da URI e sim o estilo REST.
    - Outra nota: não percebi o que ele quis dizer com "Middleware arguments"
- Página 6: não me recordo de usar querySelector nas aulas e nos exames (talvez algum exame antigo?)
- Página 7, em 5.4, poderia ter exemplos das chamadas das funções.
    - Em 5.5, também poderia ter exemplo de protótipo.
- Página 9 e 10:
    - Erro: Promise.all não executa em "paralelo". O termo correto é concorrente.
- Página 13:
    - 9.8: mistura tratamento de erros em Promises com Express, deixando o conteúdo confuso.
- Página 14:
    - A secção 9.9 trata de injeção de dependências, mas não explica o conceito e nem aponta um exemplo.
- Página 15: secção 11.3 trata do Passport.js, mas não explica quase nada sobre ele.
    - Não lembro de ser cobrado em exames.
- Página 16: secção 12.3 trata de injeção de dependências em módulos mas o exemplo é pobre (não verifica a dependência, por exemplo).
    - Há um erro na afirmação pois não é uma função construtora (a construção é irrelevante, pois a função não será usada para construir objeto).
        - É apenas uma função de init que deve ser exportada como default. 
        - O default é relevante para a injeção de dependências pois permite estabelecer a única interface com o módulo. 
- No geral, cobre boa parte do conteúdo de aula, material produzido apresenta alguns erros e vai um pouco além do conteúdo de aula (há partes que não são cobradas nos exames).
- Nota: 3/5

## DeepSeek

Página 1:
    - 1.2: tecnicamente, o GET pode ter body (o protocolo não impede isso). Mas conceptualmente, não faz sentido ter.
    . 1.2: definição de idempotente é fraca ("o efeito no servidor" ficaria melhor).
Página 2: usa o termo "Middleware arguments" também deixando claro que é algo do Express.
    - Não explica relação dos argumentos de midleware com o formato do URI.
Página 3:
    - querySelector novamente -> deve ser de alguma prova antiga, pois isso não tem sido cobrado nos últimos anos.
    - Explica mal alguns conceitos de funções em JS.
Página 4:
    - "[VERIFICAR: Uma função que retorna uma Promise não precisa obrigatoriamente de async — pode retornar explicitamente new Promise()]"
        - A LLM não sabe se isso é verdade? Achei estranha essa parte.
    - Em 3.4, poderia dar exemplos de uso dos métodos.
- Página 6: outro VERIFICAR: "[VERIFICAR: Funções síncronas existem (fs.readFileSync), mas bloqueiam a thread.]"
- Página 6: secção 6: não trata de middleware error handler (com quarto argumento).
- Página 9: secção 9: falta explicar como é um pedido com o header authorization (e.g., tipo Bearer). Explicar o módulo passport e cookie-session. Explicar um exemplo de pedido set-cookie e cookie.
- Página 11: Melhorar explicação sobre o uso do this.
- No geral, tem menos erros mas a cobertura de conteúdo é mediana.
- Nota: 3/5

## LeChat

- Página 1: correção: o PATCH pode ser idempotente (ou não)
    - Definição de idempotente está incompleta.
- Página 2: secção 1.4: a descrição do content-type: ... tem pedido/pedido ao invés de pedido/resposta.
- Página 3: secção 3.2: importante apresentar o retorno das funções das Promises.
- Página 4: secção 3.3: descrição de async/await incompleta.
- Página 5: secção 5: explicar os argumentos do middleware: err, req, res, next
- Página 6: secção 7: informação incompleta sobre o armazenamento de tokens. Em termos de segurança, o token deve ser armazenado apenas na sessão do cliente e usa-se um session_id como cookie. O conteúdo é ensinado assim.
- Página 6: secção 8.1: usa biblioteca Joi (que não é usada na UC).
- Página 7: secção 8.2: tratar erro com catch de uma Promise explícita também (não apenas try/catch).
- Página 8: aborda handlebars, mas não explica nada sobre HTML e CSS (os testes cobram isso também).
- No geral, o conteúdo apresentado é limitado e não abrange todo o conteúdo cobrado nos testes. Há algumas incorreções e informações incompletas.
- Nota: 2/5

