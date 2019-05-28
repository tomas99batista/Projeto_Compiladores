# Linguagem
## Unidades
Comprimento -> metro (m)
Massa -> grama (g)
Tempo -> segundo (s)

## Variáveis
var varName $unidade = x;  // Definição da variável, unidade e valor
    varName = z;    // Redefinição do valor da variável mas não da unidade
    varName $outraUnidadeCompativel;    // Redefinição da unidade da variável para outra unidade compatível

// Exemplo:
var p $m = 5;    // p = 5 metros

### Operação com variáveis
SI = grama | metro | segundo
$SI x $SI => $SI
$SI x $NaoSI => $SI
$NaoSI x $NaoSI => $SI
    mas: se ambas as NaoSI forem iguais entao fica => $SI

Caso especifiado qual a unidade que quer, converte para a unidade pedida, exemplo:
$m($mm x $mm) // Converte para metros
var p $mm = $m x $m //Converte para milímetro porque foi pedido na definição da variável

## Colections
var varName $unidade = [x, y, z]    // x, y, z têm todos a mesma unidade, impossível fazer um array de unidades diferentes
p.add($unidade) => adiciona ao array
p.addConverting($unidadeCompativel) => adiciona ao array mas converte para a unidade definida

## Iterators
for () { }
var p $m = [array]
for(var i in p) => i toma a $unidade de p, neste caso metros

for(var i $cm in p) => como $cm é compatível com $m, i toma os valores convertidos p para $cm

while () { }

## Statements
if () { }

if () { 
} else { }

elif () { }

switch () {
    case 'x':
        break;
}

## Comparadores
<, >, >=, <=, ==, !=, ===, !==

== / !=     Compara o valor e a $unidade
=== / !===  Compara apenas a $unidade

## Booleanos
true / false

Pode ser usado com if/else statements

## Unidades
$unidade #y     //fica 10x y acima da unidade atual
    // y pode ser positivou ou negativo
Exemplo:
$m #1   // 10x acima do metro
$m #-1  // 10x abaixo do metro

## Funções
function funtionName ($unidade arg // arg){
    ...
    return $unidade
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