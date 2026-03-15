%{
#include "parser.tab.h"
#include <stdlib.h>
#include <string.h>

int yycol = 1;
#define YY_USER_ACTION { yycol += yyleng; }
%}

%option noyywrap
%option yylineno

%%
"lambda"                { printf("TOKEN|2|lambda|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return LAMBDA; }
[a-zA-Z_][a-zA-Z0-9_]*  { printf("TOKEN|1|идентификатор|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); yylval = strdup(yytext); return ID; }
"="                     { printf("TOKEN|3|знак равенства|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return ASSIGN; }
","                     { printf("TOKEN|4|запятая|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return COMMA; }
":"                     { printf("TOKEN|5|двоеточие|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return COLON; }
"+"                     { printf("TOKEN|6|знак сложения|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return PLUS; }
"("                     { printf("TOKEN|7|открывающая скобка|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return LPAREN; }
")"                     { printf("TOKEN|8|закрывающая скобка|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return RPAREN; }
"*"                     { printf("TOKEN|9|знак умножения|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return MULT; }
" "                     { printf("TOKEN|10|пробел| |%d:%d\n", yylineno, yycol - yyleng); /* не возвращаем токен */ }
";"                     { printf("TOKEN|11|точка с запятой|%s|%d:%d\n", yytext, yylineno, yycol - yyleng); return SEMICOLON; }
\n                      { yycol = 1; }
.                       { printf("ERROR|Лексическая ошибка|Недопустимый символ '%s'|%d:%d\n", yytext, yylineno, yycol - yyleng); }
%%