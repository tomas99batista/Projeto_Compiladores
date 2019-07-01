grammar Unidades;

main: main: (expr|'\n')* EOF;

expression: assign
            | print
            | iterator
            | statement
            | comparator
            | function
            | collections
            ;

assign: ID GRANDEZA '=' VALUE #AssignValue
        | ID '=' VALUE        #RedefineValue
        | ID GRANDEZA         #RedefineGreatness
        ;

        

GRANDEZA: MASSA | COMPRIMENTO | TEMPO
fragment MASSA: UNIDADE 'g' 
fragment COMPRIMENTO: UNIDADE 'm'
fragment TEMPO: UNIDADE 's'
fragment UNIDADE: 'k' | 'h' | 'da' | 'd' | 'c' | 'm' | '':

iterator: for
          | while
          ;

for: 'for' '(' FORSTATEMENT ')' '{' FORCODE '}';          #For
fragment FORSTATEMENT:
fragment FORCODE:

while: 'while' '(' WHILESTATEMENT ')' '{' WHILECODE '}';  #While
fragment WHILESTATEMENT:
fragment WHILECODE:

statement: 'if' '(' IFSTATEMENT ')' '{' CODE '}'
           | 'elif' '(' IFSTATEMENT ')' '{' CODE '}'
           | 'else' '{' CODE '}'
           ;
fragment IFSTATEMENT:
fragment CODE:

ID : LETTER | (LETTER | DIGIT)*;
fragment LETTER : [a-zA-Z_]+;
fragment DIGIT : [0-9]+;

STRING : '"' (ESC | . )*? '"';
fragment ESC: '\\"' | '\\\\';

//Whitespaces & Comments
LINE_COMMENT : '//' .*? '\n' -> skip;
COMMENT : '/*' .*? '*/' -> skip;
WS : [ \t\r\n]+ -> skip ;
            
