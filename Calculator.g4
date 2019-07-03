grammar Calculator ;


program: main* EOF;


main
	: assignment
	| print
	| expr
    //| iterator
    | statement
    ;


assignment
		: 	ID '=' expr			#AssignValue
        | 	ID 'to' ID         	#RedefineGreatness	//segundo id indica a grandeza
        | 	expr 'to' ID 		#Convert
        ;


expr	
	: left=expr op=('*'|'/') right=expr		# MulDiv
	| left=expr op=('+'|'-') right=expr		# AddSub
	| valor									# Uni
	| '(' expr ')'							# Para
	| ID 									# variable
	;

print: 'print' (expr|assignment) ;

valor: Number ID;			// O id é usado para definir a grandeza


statement:	'if' '(' condition ')' '{' main '}'
			('elif' '(' condition ')' '{' main '}')*
           ('else' '{' main '}')?
           ;

forStatement: 'for' '(' condition ')' '{' main '}';

whileStatement: 'while' '(' condition ')' '{' main '}';


condition: left=expr Comparator right=expr;
Comparator: ('>'|'>='|'=='|'!='|'<='|'<');


ID : [A-Za-z_] [A-Za-z_0-9/]* ;
Number : [0-9]+ ('.' [0-9]+)*;
WS: [ \n\t]+ -> skip ;