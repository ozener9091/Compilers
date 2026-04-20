package parser;

import org.junit.jupiter.api.Test;
import parser.ast.ProgramNode;
import parser.semantic.SemanticAnalyzer;
import scanner.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LambdaParserTest {

    @Test
    void parsesValidLambdaAssignment() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a, b, c: a + (b * c);");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertTrue(lexical.getErrors().isEmpty());
        assertTrue(parseResult.getErrors().isEmpty());
        assertTrue(semanticResult.getErrors().isEmpty());
        assertEquals(1, semanticResult.getAst().getDeclarations().size());
    }

    @Test
    void reportsMissingSemicolon() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a, b: a + b");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());

        assertFalse(parseResult.isSuccess());
        assertTrue(parseResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("точка с запятой")));
    }

    @Test
    void reportsDuplicateDeclaration() {
        Scanner.LexicalResult lexical = Scanner.analyze(
                "calc = lambda a: a;\n" +
                "calc = lambda b: b;"
        );
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertTrue(lexical.getErrors().isEmpty());
        assertTrue(parseResult.getErrors().isEmpty());
        assertEquals(1, semanticResult.getAst().getDeclarations().size());
        assertTrue(semanticResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("уже объявлен")));
    }

    @Test
    void reportsUndeclaredIdentifier() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a: a + b;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertTrue(semanticResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("используется до объявления")));
    }

    @Test
    void reportsValueOutOfIntRange() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda: 999999999999999999999999;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertTrue(semanticResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("допустимые пределы")));
    }

    @Test
    void reportsDuplicateParameterInSingleScope() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a, a: a + 1;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertTrue(semanticResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("уже объявлен")));
    }

    @Test
    void rejectsLambdaUsageInArithmeticExpressionByTypeRule() {
        Scanner.LexicalResult lexical = Scanner.analyze(
                "f = lambda x: x;\n" +
                "g = lambda a: a + f;"
        );
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());
        ProgramNode semanticAst = semanticResult.getAst();

        assertEquals(1, semanticAst.getDeclarations().size());
        assertTrue(semanticResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("Несовместимость типов")));
    }
}
