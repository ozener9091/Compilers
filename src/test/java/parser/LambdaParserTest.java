package parser;

import org.junit.jupiter.api.Test;
import scanner.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LambdaParserTest {

    @Test
    void parsesValidLambdaAssignmentWithNumbers() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze("calc = lambda a, b, c: a + (b * 25);");

        assertTrue(lexicalResult.getErrors().isEmpty());
        assertTrue(LambdaParser.parse(lexicalResult.getLexemes()).isSuccess());
    }

    @Test
    void parsesValidLambdaWithEmptyParameters() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze("calc = lambda: 10 / 2;");

        assertTrue(lexicalResult.getErrors().isEmpty());
        assertTrue(LambdaParser.parse(lexicalResult.getLexemes()).isSuccess());
    }

    @Test
    void reportsMissingSemicolon() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze("calc = lambda a, b, c: a + (b * c)");

        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexicalResult.getLexemes());

        assertFalse(parseResult.isSuccess());
        assertTrue(parseResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("точка с запятой")));
    }

    @Test
    void reportsMissingCommaBetweenParameters() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze("calc = lambda a b, c: a + b;");

        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexicalResult.getLexemes());

        assertFalse(parseResult.isSuccess());
        assertTrue(parseResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("между параметрами")));
    }

    @Test
    void reportsLexicalErrorForUnsupportedCharacter() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze("calc = lambda a, b: a . b;");

        assertFalse(lexicalResult.getErrors().isEmpty());
        assertTrue(lexicalResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("Недопустимый символ")));
    }

    @Test
    void parsesMultilineInput() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze(
                "calc = lambda a, b: a + b;\n" +
                "mab = lambda sdf, fdg: sdf + (sdf + fdg);"
        );

        assertTrue(lexicalResult.getErrors().isEmpty());
        assertTrue(LambdaParser.parse(lexicalResult.getLexemes()).isSuccess());
    }

    @Test
    void parsesMultilineInputWithThreeLines() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze(
                "a = lambda x: x;\n" +
                "b = lambda y: y * 2;\n" +
                "c = lambda z: z + 1;"
        );

        assertTrue(lexicalResult.getErrors().isEmpty());
        assertTrue(LambdaParser.parse(lexicalResult.getLexemes()).isSuccess());
    }

    @Test
    void reportsErrorForInvalidSyntaxInMultiline() {
        Scanner.LexicalResult lexicalResult = Scanner.analyze(
                "a = lambda x: x;\n" +
                "b = lambda y: y *;\n" +
                "c = lambda z: z + 1;"
        );

        LambdaParser.ParseResult parseResult = LambdaParser.parse(lexicalResult.getLexemes());

        assertFalse(parseResult.isSuccess());
        assertTrue(parseResult.getErrors().stream()
                .anyMatch(error -> error.getDescription().contains("Ожидался")));
    }
}
