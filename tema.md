# Projeto Compiladores

## Objetivos
O trabalho a desenvolver deve envolver, pelo menos, duas linguagens: 
    - uma para um compilador (que, em princı́pio, será a principal linguagem do trabalho)
    - outra para ler informação estruturada (por exemplo, um ficheiro de configuração, ou uma linguagem de especificação complementar à linguagem principal).

Fases:
1. Concepção e definição de uma linguagem de programação (sintaxe e semântica).
2. Implementação em ANTLR4 da análise léxica e da análise sintáctica de um compilador para a linguagem;
3. Definição das regras semânticas a aplicar à linguagem, e sua implementação no contexto do ponto anterior.
4. Escrita de documento que descreva a linguagem (instruções existentes e o seu significado; exemplos de programas; etc.).
5. Escolha criteriosa de uma linguagem destino, onde se possa implementar a sı́ntese (backend ) do compilador.
6. Definição dos padrões de geração de código para as instruções da linguagem.
7. Concretização completa do compilador.

## Tema
Linguagem para análise dimensional (fı́sica). A especificação de dimensões e unidades (metros, segundos, nano, micro, ...) pode ser feita numa linguagem separada, sendo o compilador aplicável a uma linguagem de programação que se aproxime qb. de uma linguagem de uso genérico

Implementação operações:
• Definição de variáveis;
• Operações interactivas com o utilizador;
• Definição de expressões que definam uma álgebra sobre elementos da linguagem (números, figuras, tabelas, imagens, ...);
• Instruções iterativas;
• Expressões booleanas (predicados) e instruções condicionais;
• Funções.

## Avaliação

1. Concepção da linguagem. A simplicidade e expressividade da linguagem definida serão aspectos a valorizar.
2. Gramáticas desenvolvidas.
3. Análise semântica.
4. Gestão de erros.
5. Legibilidade do código e documentação.
6. Geração de código (será valorizado o uso de uma linguagem destino mais “baixo nı́vel”).

## Linguagem para análise dimensional
Pretende-se estender o sistema de tipos de uma linguagem de programação (tanto quanto baste, de uso geral) com a possibilidade de definir dimensões distintas (e interoperáveis) a expressões numéricas (e como tal, a variáveis e outras entidades com tipos da linguagem). 1
Por exemplo, poder definir tipos de dados distância e tempo (expressáveis, por exemplo, com unidades metro [m] e segundo [s]) e poder definir um novo tipo de dados velocidade como sendo distância/tempo [m/s]. O sistema de tipos da linguagem não só deve permitir álgebra sobre dimensões existentes, com validar a respectiva correcção (atribuir e/ou somar distância com tempo não deve ser permitido, muito embora ambos sejam números).
Muito embora seja na área da fı́sica que as questões dimensionais sejam mais conhecidas, na realidade todos os programadores são confrontados com este tipo de problemas. Por exemplo, sempre que definimos uma variável inteira para percorrer ı́ndices de um array com um histograma de idades, não faz sentido que ela possa ser misturada com outra variável, também inteira, mas que represente o número de porcos de uma quinta.
Seria assim interessante haver soluções práticas para este tipo de problemas (o uso do sistema de tipos estáticos de linguagens de programação seria uma solução com grandes potencialidades).
Poder-se-ia ter uma linguagem de especificação (interpretada) para definir dimensões e unidades, que definiria novos tipos de dados (numéricos) que poderiam ser utilizados com segurança na linguagem genérica (compilada), evitando dessa forma, erros dimensionais nesse código.

Seria necessário aplicar uma álgebra dimensional (neste projecto, aplicável apenas a números inteiros e reais) com as seguintes regras:
• somas e subtracções apenas para a mesma dimensão (ex: 1s + 50ms, para a dimensão tempo);
• multiplicações e divisões aplicáveis a expressões dimensionais a gerar outra dimensão (ex: 1m/10s, a gerar a dimensão m/s);
• multiplicações e divisões por expressões adimensionais a manter a mesma dimensão (ex: 5s*100).