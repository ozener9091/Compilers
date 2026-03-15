%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern int yylex();
void yyerror(const char *s);
extern int yylineno;
extern int yycol;

#define YYSTYPE char*
%}

%token ID
%token LAMBDA
%token ASSIGN
%token COMMA
%token COLON
%token PLUS
%token LPAREN
%token RPAREN
%token MULT
%token SEMICOLON

%start program

%%

program
    : statement
    | program statement
    ;

statement
    : ID ASSIGN expression SEMICOLON {
        free($1);
    }
    ;

expression
    : lambda_expr
    | additive_expr
    ;

lambda_expr
    : LAMBDA id_list COLON expression {
        free($2);
    }
    ;

id_list
    : ID {
        $$ = $1;
    }
    | id_list COMMA ID {
        free($1);
        $$ = $3;
    }
    ;

additive_expr
    : multiplicative_expr
    | additive_expr PLUS multiplicative_expr
    ;

multiplicative_expr
    : primary
    | multiplicative_expr MULT primary
    ;

primary
    : ID {
        free($1);
    }
    | LPAREN expression RPAREN
    ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "ERROR|Синтаксическая ошибка|%s|%d:%d\n", s, yylineno, yycol);
}

int main() {
    return yyparse();
}