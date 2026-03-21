parser grammar LambdaParser;

options { tokenVocab=LambdaLexer; }

@header {
package antlr;
}

program: statement+ EOF;

statement: identifier ASSIGN lambdaExpression SEMICOLON;

lambdaExpression: LAMBDA parameters COLON expression
                | parameters COLON expression
                ;

parameters: parameterList?;

parameterList: parameter (COMMA parameter)* COMMA?;

parameter: identifier;

expression: term ((PLUS | MINUS) term)*;

term: factor ((STAR | SLASH) factor)*;

factor: NUMBER
      | identifier
      | LPAREN expression RPAREN
      ;

identifier: IDENTIFIER;
