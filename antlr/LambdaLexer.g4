lexer grammar LambdaLexer;

@header {
package antlr;
}

LAMBDA: 'lambda';

ASSIGN: '=';
COMMA: ',';
COLON: ':';
PLUS: '+';
MINUS: '-';
STAR: '*';
SLASH: '/';
LPAREN: '(';
RPAREN: ')';
SEMICOLON: ';';

IDENTIFIER: LETTER (LETTER | DIGIT | '_')*;

NUMBER: DIGIT+;

WS: [ \t\r\n]+ -> skip;

UNKNOWN: .;

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];
