package parser;

import org.junit.jupiter.api.Test;
import parser.ir.IntermediateCodeGenerator;
import parser.semantic.SemanticAnalyzer;
import scanner.Scanner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void supportsExtendedMultiplicativeOperators() {
        String source = "calc = lambda a, b, c: (a // b) + (c % 2) ** 3;";
        Scanner.LexicalResult lexical = Scanner.analyze(source);
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());

        assertTrue(lexical.getErrors().isEmpty());
        assertTrue(parseResult.getErrors().isEmpty());
        assertEquals(1, parseResult.getAst().getDeclarations().size());
    }

    @Test
    void scannerRecognizesCompoundOperators() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda: 10 // 3 % 2 ** 2;");
        List<Scanner.TokenKind> kinds = lexical.getLexemes().stream()
                .map(Scanner.Lexeme::getKind)
                .toList();

        assertTrue(kinds.contains(Scanner.TokenKind.DOUBLE_SLASH));
        assertTrue(kinds.contains(Scanner.TokenKind.PERCENT));
        assertTrue(kinds.contains(Scanner.TokenKind.DOUBLE_STAR));
    }

    @Test
    void reportsMissingSemicolon() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a, b: a + b");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());

        assertFalse(parseResult.isSuccess());
        assertTrue(parseResult.getErrors().stream()
                .anyMatch(error -> "Синтаксическая ошибка".equals(error.getType())));
    }

    @Test
    void buildsQuadruplesAndPolizForNumericExpression() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda: 2 + 3 * 4;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());

        assertTrue(lexical.getErrors().isEmpty());
        assertTrue(parseResult.getErrors().isEmpty());

        IntermediateCodeGenerator.ProgramIrResult ir = IntermediateCodeGenerator.build(parseResult.getAst());
        assertEquals(1, ir.getDeclarations().size());

        IntermediateCodeGenerator.LambdaIrResult declaration = ir.getDeclarations().getFirst();
        assertEquals(2, declaration.getQuadruples().size());
        assertEquals(List.of("2", "3", "4", "*", "+"), declaration.getPoliz());
        assertEquals(14L, declaration.getEvaluatedValue());
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
        assertFalse(semanticResult.getErrors().isEmpty());
    }

    @Test
    void reportsUndeclaredIdentifier() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a: a + b;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertFalse(semanticResult.getErrors().isEmpty());
    }

    @Test
    void reportsValueOutOfIntRange() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda: 999999999999999999999999;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertFalse(semanticResult.getErrors().isEmpty());
    }

    @Test
    void reportsDuplicateParameterInSingleScope() {
        Scanner.LexicalResult lexical = Scanner.analyze("calc = lambda a, a: a + 1;");
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertFalse(semanticResult.getErrors().isEmpty());
    }

    @Test
    void rejectsLambdaUsageInArithmeticExpressionByTypeRule() {
        Scanner.LexicalResult lexical = Scanner.analyze(
                "f = lambda x: x;\n" +
                        "g = lambda a: a + f;"
        );
        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexical.getLexemes());
        SemanticAnalyzer.SemanticResult semanticResult = SemanticAnalyzer.analyze(parseResult.getAst());

        assertNotNull(semanticResult.getAst());
        assertFalse(semanticResult.getErrors().isEmpty());
    }
}
