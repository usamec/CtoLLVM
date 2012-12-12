grammar projekt;

options {
  output=AST;
//  backtrack=true;
}

tokens {
  DEC;
  BLI;
  UNARYPLUS;
  UNARYMINUS;
  UNARYADDRESS;
  UNARYDEREFERENCE;
  COMPOUND;
  FUNCDEF;
  FUNCDEC;
  FUNCCALL;
  PDEC;
  ARRAYSUBS;
  IDEC;
  EMPTYSTAT;
  ARRAYDEC;
  POINTER;
  STORAGE_SPECIFIER;
}

@parser::header {
  package ctollvm.parser;
  import ctollvm.*;
}

@lexer::header {
  package ctollvm.parser;
  import ctollvm.*;
}
//parse
//  :  (t=. 
//          {System.out.printf("text: \%-7s  type: \%s \n", 
//           $t.text, tokenNames[$t.type]);}
//     )* 
//     EOF
//  ;

parse  
:  external_declaration* EOF! 
;

//original OR is left-recursive
logical_or_expression
:	(logical_and_expression) ('||'^ logical_and_expression)*
	;	 
//
//	
logical_and_expression
: 	(inclusive_or_expression) ('&&'^ inclusive_or_expression)*
	;
//	
inclusive_or_expression
:	(exclusive_or_expression)// ('|' exclusive_or_expression)*
	;
//
exclusive_or_expression
: 	(and_expression)// ('^' and_expression)*	
	;
//
and_expression
:	(equality_expression)// ('&' equality_expression)*
	;
//
equality_expression
:	(relational_expression) (('!=' | '==')^ relational_expression)*
	;
//	
//	
//
relational_expression
:	(shift_expression) (('<=' | '>=' | '<' | '>')^  shift_expression)*
	;
//
shift_expression
:	(additive_expression)// (Shift_operator additive_expression)*
	;
//	
additive_expression
:	(multiplicative_expression) (('+' | '-')^ multiplicative_expression)*
	;
	
multiplicative_expression
:	(cast_expression) (('*' | '/' | '%')^ cast_expression)*
	;

cast_expression
:	unary_expression
//	|	'(' type_name ')' cast_expression
	;	

unary_expression
:	postfix_expression
//	|'++' unary_expression
//	|'--' unary_expression
//	| ('!' | '~' | '*' | '&'  | '-' | '+') cast_expression
//	| ('*' | '&'  | '-' | '+') cast_expression -> ^(UNARY ('*' | '&'  | '-' | '+')
//        cast_expression)
        | '&' cast_expression -> ^(UNARYADDRESS cast_expression)
        | '*' cast_expression -> ^(UNARYDEREFERENCE cast_expression)

//	| 'sizeof' unary_expression
//	| 'sizeof '(' type_name ')'
	;
	
postfix_expression
//	: (primary_expression | '(' type_name ')' '{' initializer_list+ '}') ('[' expression ']' | '(' argument_expression_list? ')' | '.' Identifier | '-'> Identifier | '++' | '--')*
        : (primary_expression -> primary_expression) 
          ( '(' a=argument_expression_list? ')' -> 
            ^(FUNCCALL $postfix_expression $a?) |
            '[' e=expression ']' ->
            ^(ARRAYSUBS $postfix_expression $e)
          )*
	;
//
argument_expression_list
	: (assignment_expression) (','! assignment_expression)*	
	;
//

primary_expression
	: Identifier
	| Integer
        | Float
        | String_constant
        | Character_constant
        | '('! expression ')'!
	;
	
//
assignment_expression	
  : (unary_expression '=') => unary_expression ('=' | '*=' |  '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|=')^ assignment_expression	
  | conditional_expression
	;
//	
//constant_expression
//	: conditional_expression
//	;
//
conditional_expression
  : logical_or_expression
//	: logical_or_expression ( | logical_or_expression '?' expression ':' conditional_expression )
	; 	
//	
//
expression
  : 	assignment_expression //(',' assignment_expression)*
;
//	
//translation_unit:
//	(: external_declaration) (external_declaration)*
//	;
//
external_declaration options {backtrack=true;}
	: function_definition
	| declaration	
	;
//	


parameter_type_list
	: parameter_list (','! '...')?
	;

parameter_list
	: parameter_declaration (',' parameter_declaration)* -> 
        parameter_declaration (parameter_declaration)* 
  ;

// TODO: fixnut na spravny tvar
parameter_declaration
	: Type_specifier declarator -> ^(PDEC Type_specifier ^(IDEC declarator))
//	| declaration_specifiers abstract_declarator ?
	;

function_definition
        : Type_specifier declarator '{' block_item_list '}'
        -> ^(FUNCDEF Type_specifier declarator block_item_list)
//	: declaration_specifiers declarator declaration_list ? compound_statement
	;
//	
//declaration_list
//	: (declaration) (declaration)*
//	;
//		
//	
//
//
statement
//	: labeled_statement
	: compound_statement
	| expression_statement
	| selection_statement
	| iteration_statement
	| jump_statement
        | empty_statement
	;
//
//for_iteration 
//  	:  'for' '(' expression? ';' expression? ';' expression? ')' statement
//	  |  'for' '(' declaration expression? ';'expression? ')' statement
//	  ;  
//  
while_iteration  
	  :  'while'^ '('! expression ')'! statement
	  ;
  
do_iteration
	  : 'do'^ statement 'while'! '('! expression ')'! ';'!
	  ;  	
  
iteration_statement
//	: for_iteration
	: while_iteration
	| do_iteration   
	;
//
//labeled_statement
//	:  Identifier ':' statement
//	| 'case' constant_expression ':' statement
//	| 'default' : statement
//	;	
//
compound_statement
	: '{' block_item_list '}' -> ^(COMPOUND block_item_list)
	;
//
block_item_list
	: block_item* -> ^(BLI block_item*)
	;

block_item
	: (Identifier ';') => statement
	| declaration
	;
//
expression_statement
	: expression ';'!
	;	
//
empty_statement
        : ';' -> EMPTYSTAT
        ;

//
selection_statement
	: 'if'^ '('! expression ')'! statement  ('else'! statement) ?
//	| 'switch' '(' expression ')' statement
	;
//	
//
jump_statement
	: 'goto' Identifier ';' -> EMPTYSTAT  // F*ck GOTO!!!
	| 'continue' ';'!
	| 'break'  ';'!
	| 'return'^ expression ? ';'!
	;
//

declaration
// TODO: miesto Type_specifier tu dat poriadne declaration_specifiers
  : declaration_specifiers (init_declarator (',' init_declarator)*)? ';' -> 
      ^(DEC declaration_specifiers init_declarator*)
;

declarator
	: direct_declarator
        | pointer declarator -> ^(POINTER declarator)
	;

pointer
	: '*' type_qualifier_list? -> ^('*')
	;

type_qualifier_list
	: (Type_qualifier) (Type_qualifier)*
	;

direct_declarator
//      : Identifier
//      | Identifier '(' parameter_type_list? ')' -> ^(FUNCDEC Identifier parameter_type_list?)
//;
	: (Identifier -> Identifier | '(' declarator ')' -> declarator)
          ('[' type_qualifier_list ? a=assignment_expression ? ']' -> 
              ^(ARRAYDEC $direct_declarator $a?) 
          |'[' 'static' type_qualifier_list ? assignment_expression ']'
          |'[' type_qualifier_list 'static' assignment_expression ']'
          |'[' type_qualifier_list ? '*' ']'
          |'(' p=parameter_type_list? ')' -> ^(FUNCDEC $direct_declarator $p?) 
//          |'(' identifier_list ? ')'
           )*
	;

//declaration
//	: declaration_specifiers init_declarator_list ? ';'
//	;
//

// Ideme tu spravit enforcement na to, aby kazdy declaration_specifiers mal aspon jeden
// type_specifier

declaration_specifiers_before
  : (storage_class_specifier | Type_qualifier)*
;

declaration_specifiers_after
  : (storage_class_specifier | Type_qualifier | type_specifier_after)*
;

// Tu narvar struct a enum
type_specifier
  : Type_specifier
  | Identifier
;

type_specifier_after
  : Type_specifier
;

declaration_specifiers
	: declaration_specifiers_before type_specifier declaration_specifiers_after
;

//
//init_declarator_list
//	: ( init_declarator_list ',') ? init_declarator
//	;
//
init_declarator
	: declarator -> ^(IDEC declarator) //( '=' initializer ) ?
	;
//
storage_class_specifier
	: ('typedef'
	| 'extern' 
	| 'static' 
	| 'auto' 
	| 'register')
	;
//
//struct_or_union_specifier
//	: struct_or_union Identifier ? '{' struct_declaration_list '}'
//	| struct_or_union Identifier
//	;
//
//struct_or_union
//	: 'struct'
//	| 'union'
//	;
//
//struct_declaration_list
//	:(struct_declaration) (struct_declaration)*
//	;
//
//struct_declaration
//	: specifier_qualifier_list struct_declarator_list ';'
//	;
//	
//specifier_qualifier_list
//	: (Type_specifier | Type_qualifier) specifier_qualifier_list ?
//	;
//
//struct_declarator_list
//	: ( struct_declarator_list ',') ? struct_declarator
//	;
//
//struct_declarator
//	: declarator
//	| declarator ? ':' constant_expression
//	;
//
//enum_specifier
//	: 'enum' Identifier ? '{' enumerator_list (',' enumerator_list)* '}'
//	| 'enum' Identifier
//	;
//  
//enumerator_list
//	: (enumerator_list ',') ? enumerator
//	;
//  
//enumerator
//	: Enumeration_constant ('=' constant_expression) ?
//	;
//
//function_specifier
//	: 'inline'
//	;
//
//
//
//
//
//
//identifier_list
//	: (identifier_list ',' ) ?  Identifier
//	;
//
//type_name	
//	: specifier_qualifier_list abstract_declarator ?
//	;
//
//abstract_declarator
//	: pointer
//	| pointer ? direct_abstract_declarator
//	;
//
//direct_abstract_declarator
//	: ('(' abstract_declarator ')') (? '[' type_qualifier_list ? assignment_expression ? ']' | ? '[' 'static' type_qualifier_list ? assignment_expression ']' | ? '[' type_qualifier_list 'static' assignment_expression ']' | ? '[' '*' ']' | ? '(' parameter_type_list ? ')')*
//	;
//
//typedef_name
//	:Identifier
//	;
//
//initializer
//	: assignment_expression
//	| '{' initializer_list (',' initializer_list)* '}'
//	;
//
//initializer_list
//	: (initializer_list ',') ? designation ? initializer
//	;
//
//designation
//	: designator_list '='
//	;
//
//designator_list
//	: (designator) (designator)*
//	;
//
//designator
//	: '[' constant_expression ']'
//	|. Identifier
//	;
//
//		  
//	
Whitespace
: (' '
| '\t'
| '\r'
| '\n'
) {$channel=HIDDEN;}
;

Comment
: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
| '/*' ( options {greedy=false;} : .) '*/' {$channel=HIDDEN;}
;



//Constant
//	: Integer_constant
	//| Floating_constant
	//| Enumeration_constant
//	| Character_constant
//	;

//Enumeration_constant
//	: Identifier
//	;

//Integer_constant
//	: Integer
//	;

//Floating_constant
//	: Float
//	;
//
//
//Header_name:
//	: //TODO
//	;
//
String_literal
:	//TODO	   
   ;
//   
//   
//
//
fragment Digit_not_null
: '1'..'9'
;

fragment Digit
: '0'..'9'
;

fragment Hexdigit_not_null
: '1'..'9'
| 'a'..'f'
| 'A'..'F'
;

fragment Hexdigit
: '0'..'9'
| 'a'..'f'
| 'A'..'F'
;

fragment Lower
: 'a'..'z'
;

fragment Upper
: 'A'..'Z'
;

fragment Alphabetic
: '_'
| Lower
| Upper
;

Type_specifier
: 'void'
| 'char'
| 'short'
| 'int'
| 'long'
| 'float'
| 'double'
| 'signed'
| 'unsigned'
| '_Bool'
;

Keyword
: 'struct' | 'typedef' | 'union' | 'enum' | 'if' | 'for' | 'while' | 'do' | 'switch' | 'case' | 'break' | 'continue' | 'default' | 'return' | 'else' | 'sizeof' | 'inline'
;

//Assignment_operator
//: '=' | '*=' |  '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
//	;

	
//Unary_operator
//: '!' | '~' | '*' | '&'  | '-' | '+'
//;

//Multiplicative_operator
//: '*' | '/' | '%'
//;

//Aditive_operator
//: '+' | '-'
//;

//Relational_operator
//:    '<=' | '>=' | '<' | '>' 
//;

//Equality_operator
//: '!=' | '=='
//	;
                        
//Logical_operator
//:    '&&' |  '||'
//;

//Bitwise_operator
//:    '&' | '|' | '^'
//;

Shift_operator
:    '<<' | '>>'
;

Boolean
: 'true'
| 'false'
;

fragment Exponent
: ('e'|'E') ('+'|'-')? Digit+
;

fragment Float_suffix
: ('f'|'F'|'l'|'L')
;




fragment Integer_suffix
: ('u'|'U'|'l'|'L')
;

fragment Integer_body
: '0'  
| Digit_not_null Digit*
| '0' ('0'..'7')+
| '0' ('x'|'X') Hexdigit+
;

Integer
: Integer_body //Integer_suffix?
;

Float     
: Digit_not_null Digit* ('.' Digit*)? Exponent? Float_suffix?
| '0'?'.' Digit* Exponent? Float_suffix?
;

Punctuation
: '!' | '"' |  '#' | '(' | ')' | '%' | '&' | '\'' | '*' | '+' | ',' | '-' | '.' | '/' | ':'| ';' | '<' | '=' | '>' | '?' | '[' | '\\' | ']' | '^' | '{' | '|' | '}' | '~'
;

fragment Char
: Alphabetic
| Digit
| Punctuation
;


fragment Simple_escape_sequence
: '\\\'' | '\\"' | '\\?' | '\\\\' | '\\a' | '\\b' | '\\f' | '\\n' | '\\r' | '\\t' | '\\v'
;

fragment Octal_escape_sequence
: '\\'('0'..'7') | '\\'('0'..'7')('0'..'7') | '\\'('0'..'7')('0'..'7')('0'..'7')
;

fragment Hexadecimal_escape_sequence
: '\\x' Hexdigit+
;

fragment Escape_sequence
: Simple_escape_sequence //| Octal_escape_sequence | Hexadecimal_escape_sequence
;

fragment C_char
: ~('\'' | '\\' | '\n') | Escape_sequence
;

Character_constant
@after {
  setText(Util.unescapeCString(getText().substring(1, getText().length()-1)));  
}
: '\''C_char'\''
;

fragment S_char
: ~('"' | '\\' | '\n') | Escape_sequence
;

String_constant
@after {
  setText(Util.unescapeCString(getText().substring(1, getText().length()-1)));
}
//@after {
//  setText(getText().substring(1, getText().length()-1).replaceAll("\\\\\\\\", "\\\\").
//      replaceAll("\\\\'", "\\\\27").replaceAll("\\\\\"", "\\\\22").replaceAll("\\\\\\?", "\\\\3f").
//    replaceAll("\\\\a", "\\\\07").replaceAll("\\\\b", "\\\\08").replaceAll("\\\\f", "\\\\0c").
//    replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\\\\0d").replaceAll("\\\\t", "\\\\09").
//    replaceAll("\\\\v", "\\\\0b"));
//}
: '"' S_char* '"' 
;



Type_qualifier
: 'const'
| 'volatile'
| 'restrict'

;

////tTODO
//// 'register'
////| 'auto' sizeof
//
Identifier
: Alphabetic (Alphabetic | Digit)*
;

