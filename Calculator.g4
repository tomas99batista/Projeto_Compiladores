grammar Calculator ;


program: main* EOF;


main
	: assignment
	| print
	| expr
    | for_statement
    | statement
    | while_statement
    ;


assignment
//		:   array				#AssignArray
		: 	ID '=' expr			#AssignValue
        | 	ID 'to' ID         	#RedefineGreatness	//segundo id indica a grandeza
        | 	expr 'to' ID 		#Convert
        ;


expr	
	: left=expr op=('*'|'//') right=expr	# MulDiv
	| left=expr op=('+'|'-') right=expr		# AddSub
	| valor									# Uni		// ex: 1 m
	| Number								# Number	// ex: 1.4 
	| '(' expr ')'							# Para
	| ID 									# variable	// ex: a
	;

print: 'print' (expr|assignment) ;

valor: Number ID ;	// O id é usado para definir a grandeza



for_statement: 'for' '(' ID '=' expr ',' condition ',' ID '=' expr ')' '{' main+ '}';          

while_statement: 'while' '(' condition ')' '{' main+ '}';



statement:		'if' '(' condition ')' '{' main+ '}'
				('elif' '(' condition ')' '{' main+ '}')*
           		('else' '{' main+ '}')?
           ;

forStatement: 'for' '(' condition ')' '{' main '}';

whileStatement: 'while' '(' condition ')' '{' main '}';


condition: left=expr Comparator right=expr;

//array:	ID '=' '[' expr+ ']';

Comparator: ('>'|'>='|'=='|'!='|'<='|'<');


ID : [A-Za-z_] [A-Za-z_0-9]* ('^' Number)? ('/' ID)? ;
Number : '-'? [0-9]+ ('.' [0-9]+)*;
COMMENTS: '#'  ~[\r\n]* -> skip;
WS: [ \r\n\t]+ -> skip;