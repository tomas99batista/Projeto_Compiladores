grammar Calculator ;


program: main* EOF;


main
	: assignment
	| print
	| expr
    //| for_statement
    | statement
    | while_statement
    ;


assignment
		: 	ID '=' expr			#AssignValue
        | 	ID 'to' ID         	#RedefineGreatness	//segundo id indica a grandeza
        | 	expr 'to' ID 		#Convert
        ;


expr	
	: left=expr op=('*'|'//') right=expr	# MulDiv
	| left=expr op=('+'|'-') right=expr		# AddSub
	| valor									# Uni
	| Number								# Number
	| '(' expr ')'							# Para
	| ID 									# variable
	;

print: 'print' (expr|assignment) ;

valor: Number ID ;	// O id é usado para definir a grandeza



//for: 'for' '(' FORSTATEMENT ')' '{' main+ '}';          

while_statement: 'while' '(' condition ')' '{' main+ '}';



statement:		'if' '(' condition ')' '{' main '}'
				('elif' '(' condition ')' '{' main '}')*
           		('else' '{' main '}')?
           ;


condition: left=expr Comparator right=expr;
Comparator: ('>'|'>='|'=='|'!='|'<='|'<');


ID : [A-Za-z_] [A-Za-z_0-9]* ('^' Number)? ('/' ID)? ;
Number : '-'? [0-9]+ ('.' [0-9]+)*;
WS: [ \n\t]+ -> skip ;