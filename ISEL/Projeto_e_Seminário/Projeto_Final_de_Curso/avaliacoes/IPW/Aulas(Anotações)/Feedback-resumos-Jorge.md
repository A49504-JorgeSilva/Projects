# IPW (conteúdo: notas de aula de 24/25)

## DeepSeek:

- Nota global: 2/5
- Fiel ao conteúdo: 1/5
- As lições apresentadas não correspondem às lições do material da UC.
- Resumo apresenta exemplos de código, o que é bom.
- Não usa conteúdo do material em todas as lições.
    - Há até lições inventadas.
- Começa na lição sobre HTTP (Lesson 01??)
    - Não trata das URIs
- Lesson 02: HTML (ok)
- Lesson 03: trata apenas de forms.
- Lesson 04: vai um pouco além do conteúdo apresentado (de forma bem resumida), mas ok.
- Lesson 04-extra: foca em CSS Layout (grid e flexbox)
    - Não me lembro de ter isso no material
- Lesson 05: JS e DOM
    - DOM não é muito explorado na UC, mas ok.
- Lesson 06: JS e eventos assíncronos
    - Promises não é explicada.
    - Foco em async/await.
- Lesson 07: Node.JS e NPM (e módulos)
    - A UC requer uso de ESM e não CommonJS (foco do resumo)
    - Não explica o uso do NPM
- Lesson 08: Express
    - Não explica seu funcionamento
- Lesson 09: Router (não há isso no material)
    - Não aborda o desenvolvimento de middlewares, middlewares de erro, ...
- Lesson 10: MongoDB e Mongoose???
    - Isto não faz parte do conteúdo.
    - Usamos elasticSearch.
- Lesson 11: Autenticação e Sessões
    - Muito básica a parte sobre cookies e sessions.
    - Becrypt?? Não é foco.
    - Usamos a framework Passport, o que não é abordado.
- Lesson 12: APIs RESTFul
    - Mal conteúdo.
- Lesson 13: Segurança
    - Não há na UC.
- Lesson 14: Deployment
    - Não há na UC.
    


## Claude:

- Nota global: 4/5
- Fiel ao conteúdo: 5/5
- A maioria das lições está bem para um resumo.
    - Cada lição ocupa 1 página ou 1 página e meio.
    - Parece fiel ao conteúdo da UC.
- Lesson 00: ok
- Lesson 01: ok
- Lesson 02: ok
- Lesson 03: ok (achei confusa a parte dos escopos para var/let)
- Lesson 04: ok
- Lesson 05: ok (faz ligação com as Lessons 03 e 04)
- Lesson 06: ok (replica os exercícios) 
- Lesson 07: ok
- Lesson 08: ok
- Lesson 09: ok
- Lesson 10: ok
- Lesson 11: ok
- Lesson 12: ok (exemplo de OpenAPI está desformatado)
- Lesson 13: ok
- Lesson 14: ok
- Lesson 15: ok
- Lesson 16: ok
- Lesson 17: ok
- Lesson 18: ok (poderia descrever melhor os selectores CSS)
- Lesson 19: ok
- Lesson 20: ok
- Ao fim, faz um tabela com resumo do objetivo das versões da App Tasks

## LeChat:

- Nota global: 2/5
- Fiel ao conteúdo: 2/5
    - Apenas nas primeiras lições.
- Lesson 00: apresenta apenas bibliografia.
- Lesson 01: ok
- Lesson 02: não apresenta as estruturas condicionais e repetição.
- Lesson 03: não trata de closure e funções de construção.
- Lesson 04: não trata do this e não descreve bem os objetos.
- Lesson 05: apresenta apenas export default. Não trata do simples export/import.
- Lesson 06: ok
- Lesson 07: não apresenta o funcionamento dos métodos (retorno, argumentos). Não trata de funções de primeira ordem.
- Lesson 08: sobre HTTP, não descreve os métodos, status code. Sobre Express, não descreve bem como definir os endpoints da API. Já introduz middleware sem explicar (express.json). URI?
- Lesson 09: ok, mas não apresenta middlewares já existentes, como json, urlencoded.
- Lesson 10: define mal REST: não é baseada em HTTP, mas pode-se usar HTTP para o estilo de arquitetura. Não trata das URIs.
- Lesson 11: não usamos `require`. Não explica nada... Como funciona passaport, cookies, cookie-session.
- Lesson 12: usamos apenas Mocha. Não apresenta seu funcionamento.
- Lesson 13: usamos ElasticSearch por ter API HTTP. Não usamos SQL e nem MongoDB.
- Lesson 14: Deploy?? Não há essa lição.
- A partir da lição 14, sai completamente da matéria da UC.


# SegInf (conteúdo: Slides de aula)

## LeChat:

- Nota global: 1/5
- Fiel ao conteúdo: 1/5
- Parece mais uma lista de conteúdo que um resumo para auxiliar na matéria.
    - Pilares de segurança (Autenticidade, confidencialidade...)
    - OAuth 2.0 e OIDC
    - RBAC
    - PKI
    - TLS 1.3 (não explica protocolo)
    - JCA (exemplo de geração de chave AES e cifra com essa chave)

## DeepSeek:

- Nota global: 1/5
- Fiel ao conteúdo: 2/5 
- Um pouco melhor que o LeChat, mas também se assemelha a uma lista de contaúdo.
    - Criptografia: não explica algoritmos (foca apenas em definições)
    - PKI e certificados: tão resumido que não sei se dá para aprender algo.
    - TLS/SSL: não explica o protocolo (apenas handshake RSA-based)
    - Passwords: apenas cita os problemas de segurança e as soluções.
    - Controlo de Acesso: apenas define o RBAC (não descreve matricial, ACL) e não descreve os modelos (define RBAC0, RBAC1 e RBAC2).
    - OAuth 2.0 e OIDC: define OAUth, apresenta fluxo Authorization Code Grant (em 4 etapas), e define OIDC e ID token
    - Há uma revisão básica de JCA sobre cifra e com exemplo de validação de cadeia de certificação PKIX
