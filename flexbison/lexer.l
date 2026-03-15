%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ID          1
#define LAMBDA      2
#define ASSIGN      3
#define COMMA       4
#define COLON       5
#define PLUS        6
#define LPAREN      7
#define RPAREN      8
#define MULT        9
#define SPACE       10
#define SEMICOLON   11

int yycol = 1;
#define YY_USER_ACTION { yycol += yyleng; }
%}

%option noyywrap
%option yylineno

%%
"lambda"                { printf("TOKEN|%d|lambda|%s|%d:%d\n", LAMBDA, yytext, yylineno, yycol - yyleng); }
[a-zA-Z_][a-zA-Z0-9_]*  { printf("TOKEN|%d|идентификатор|%s|%d:%d\n", ID, yytext, yylineno, yycol - yyleng); }
"="                     { printf("TOKEN|%d|знак равенства|%s|%d:%d\n", ASSIGN, yytext, yylineno, yycol - yyleng); }
","                     { printf("TOKEN|%d|запятая|%s|%d:%d\n", COMMA, yytext, yylineno, yycol - yyleng); }
":"                     { printf("TOKEN|%d|двоеточие|%s|%d:%d\n", COLON, yytext, yylineno, yycol - yyleng); }
"+"                     { printf("TOKEN|%d|знак сложения|%s|%d:%d\n", PLUS, yytext, yylineno, yycol - yyleng); }
"("                     { printf("TOKEN|%d|открывающая скобка|%s|%d:%d\n", LPAREN, yytext, yylineno, yycol - yyleng); }
")"                     { printf("TOKEN|%d|закрывающая скобка|%s|%d:%d\n", RPAREN, yytext, yylineno, yycol - yyleng); }
"*"                     { printf("TOKEN|%d|знак умножения|%s|%d:%d\n", MULT, yytext, yylineno, yycol - yyleng); }
" "                     { printf("TOKEN|%d|пробел| |%d:%d\n", SPACE, yylineno, yycol - yyleng); }
";"                     { printf("TOKEN|%d|точка с запятой|%s|%d:%d\n", SEMICOLON, yytext, yylineno, yycol - yyleng); }
\n                      { yycol = 1; }
.                       { printf("ERROR|Лексическая ошибка|Недопустимый символ '%s'|%d:%d\n", yytext, yylineno, yycol - yyleng); }
%%

int main() {
    printf("Запуск лексера\n");
    printf("Введите текст для анализа:\n");
    yylex();
    return 0;
}