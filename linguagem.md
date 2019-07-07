# Linguagem
## Unidades
Comprimento -> metro (m)

Massa -> grama (g)

Tempo -> segundo (s)


## Variáveis
varName = x unidade  // Definição da variável, unidade e valor
    
    varName to outraUnidadeCompativel    // Redefinição da unidade da variável para outra unidade compatível
    

// Exemplo:

p = 5 m    // p = 5 metros

### Operação com variáveis
SI = grama | metro | segundo

SI x SI => SI

SI x NaoSI => SI

NaoSI x NaoSI => SI

    mas: se ambas as NaoSI forem iguais entao fica => SI

Caso especifiado qual a unidade que quer, converte para a unidade pedida, exemplo:

$m($mm x $mm) // Converte para metros

p $mm = $m x $m //Converte para milímetro porque foi pedido na definição da variável

## Colections
varName = [x, y, z]    // x, y, z têm todos a mesma unidade, impossível fazer um array de unidades diferentes

p.add() => adiciona ao array

## Iterators
for () { }

p = [array]

for(i in p) => i toma a unidade de p, neste caso é por exemplo metros

while () { }

## Statements
if () { }

if () { 
} else { }

elif () { }

switch () {
    case 'x':
        break
}

## Comparadores
<, >, >=, <=, ==, !=, ===, !==

== / !=     Compara o valor e a unidade

=== / !===  Compara apenas a unidade

## Booleanos
true / false

Pode ser usado com if/else statements

## Funções
function funtionName (arg unidade // arg){

    ...
    
    return unidade
    
    return varName
    
    return valor
    
}

## Tipos
Vars, inteiros, floats, Strings, Comparadores, Iteradores, Colections, Unidades ($), Booleanos, Statements, Funções


## Erros possíveis
#### Vars
- Tipos Diferentes

- Unidades Incompatíveis

- Inconsistência de tipos 

#### Colections
- Tipos Diferentes

- OutofBound array

#### Iteradores
- Má definição

- Tipos diferentes

#### Statements
- Tipos diferentes

- Má definição

#### Comparadores
- Tipos diferentes
