package parser.semantic;

import parser.ast.BinaryOpNode;
import parser.ast.ExpressionNode;
import parser.ast.IdentifierNode;
import parser.ast.LambdaDeclarationNode;
import parser.ast.NumberLiteralNode;
import parser.ast.ParameterNode;
import parser.ast.ProgramNode;
import scanner.Scanner;

import java.util.ArrayList;
import java.util.List;

public final class SemanticAnalyzer {

    private static final long INT_MIN = Integer.MIN_VALUE;
    private static final long INT_MAX = Integer.MAX_VALUE;

    private enum ExprType {
        INT,
        LAMBDA,
        UNKNOWN
    }

    private SemanticAnalyzer() {
    }

    public static SemanticResult analyze(ProgramNode sourceProgram) {
        if (sourceProgram == null) {
            return new SemanticResult(new ProgramNode(List.of()), List.of());
        }

        SymbolTable globalTable = new SymbolTable();
        List<Scanner.ErrorInfo> errors = new ArrayList<>();
        List<LambdaDeclarationNode> acceptedDeclarations = new ArrayList<>();

        for (LambdaDeclarationNode declaration : sourceProgram.getDeclarations()) {
            int beforeStatementErrors = errors.size();

            if (globalTable.lookupCurrentScope(declaration.getName()) != null) {
                errors.add(semanticError(
                        declaration.getName(),
                        "Идентификатор \"" + declaration.getName() + "\" уже объявлен ранее в этой области видимости.",
                        declaration.getLine(),
                        declaration.getColumn(),
                        declaration.getName().length()
                ));
                continue;
            }

            globalTable.enterScope();
            for (ParameterNode parameter : declaration.getParameters()) {
                if (!globalTable.declare(parameter.getName(), SymbolTable.SymbolType.INT, parameter.getLine(), parameter.getColumn())) {
                    errors.add(semanticError(
                            parameter.getName(),
                            "Идентификатор \"" + parameter.getName() + "\" уже объявлен ранее в этой области видимости.",
                            parameter.getLine(),
                            parameter.getColumn(),
                            parameter.getName().length()
                    ));
                }
            }

            ExprType bodyType = checkExpression(declaration.getBody(), globalTable, errors);
            if (bodyType == ExprType.LAMBDA) {
                errors.add(semanticError(
                        declaration.getName(),
                        "Несовместимость типов: тело лямбда-выражения должно быть типа Int.",
                        declaration.getLine(),
                        declaration.getColumn(),
                        declaration.getName().length()
                ));
            }

            globalTable.exitScope();

            if (errors.size() == beforeStatementErrors) {
                acceptedDeclarations.add(declaration);
                globalTable.declare(
                        declaration.getName(),
                        SymbolTable.SymbolType.LAMBDA,
                        declaration.getLine(),
                        declaration.getColumn()
                );
            }
        }

        return new SemanticResult(new ProgramNode(acceptedDeclarations), errors);
    }

    private static ExprType checkExpression(ExpressionNode node, SymbolTable table, List<Scanner.ErrorInfo> errors) {
        if (node instanceof NumberLiteralNode numberLiteral) {
            try {
                long value = Long.parseLong(numberLiteral.getLiteral());
                if (value < INT_MIN || value > INT_MAX) {
                    errors.add(semanticError(
                            numberLiteral.getLiteral(),
                            "Числовой литерал \"" + numberLiteral.getLiteral() + "\" выходит за допустимые пределы типа Int.",
                            numberLiteral.getLine(),
                            numberLiteral.getColumn(),
                            numberLiteral.getLiteral().length()
                    ));
                    return ExprType.UNKNOWN;
                }
                return ExprType.INT;
            } catch (NumberFormatException ex) {
                errors.add(semanticError(
                        numberLiteral.getLiteral(),
                        "Числовой литерал \"" + numberLiteral.getLiteral() + "\" выходит за допустимые пределы типа Int.",
                        numberLiteral.getLine(),
                        numberLiteral.getColumn(),
                        numberLiteral.getLiteral().length()
                ));
                return ExprType.UNKNOWN;
            }
        }

        if (node instanceof IdentifierNode identifierNode) {
            SymbolTable.Symbol symbol = table.lookup(identifierNode.getName());
            if (symbol == null) {
                errors.add(semanticError(
                        identifierNode.getName(),
                        "Идентификатор \"" + identifierNode.getName() + "\" используется до объявления.",
                        identifierNode.getLine(),
                        identifierNode.getColumn(),
                        identifierNode.getName().length()
                ));
                return ExprType.UNKNOWN;
            }

            if (symbol.type() == SymbolTable.SymbolType.LAMBDA) {
                return ExprType.LAMBDA;
            }
            return ExprType.INT;
        }

        if (node instanceof BinaryOpNode binaryOpNode) {
            ExprType leftType = checkExpression(binaryOpNode.getLeft(), table, errors);
            ExprType rightType = checkExpression(binaryOpNode.getRight(), table, errors);

            boolean leftOk = leftType == ExprType.INT;
            boolean rightOk = rightType == ExprType.INT;

            if (!leftOk || !rightOk) {
                if (leftType != ExprType.UNKNOWN && rightType != ExprType.UNKNOWN) {
                    errors.add(semanticError(
                            binaryOpNode.getOperator(),
                            "Несовместимость типов: арифметические операторы применимы только к типу Int.",
                            binaryOpNode.getLine(),
                            binaryOpNode.getColumn(),
                            binaryOpNode.getOperator().length()
                    ));
                }
                return ExprType.UNKNOWN;
            }

            return ExprType.INT;
        }

        return ExprType.UNKNOWN;
    }

    private static Scanner.ErrorInfo semanticError(
            String fragment,
            String description,
            int line,
            int column,
            int highlightLength
    ) {
        return new Scanner.ErrorInfo(
                "Семантическая ошибка",
                fragment,
                description,
                line,
                column,
                Math.max(highlightLength, 0)
        );
    }

    public static class SemanticResult {
        private final ProgramNode ast;
        private final List<Scanner.ErrorInfo> errors;

        public SemanticResult(ProgramNode ast, List<Scanner.ErrorInfo> errors) {
            this.ast = ast;
            this.errors = errors == null ? List.of() : List.copyOf(errors);
        }

        public ProgramNode getAst() {
            return ast;
        }

        public List<Scanner.ErrorInfo> getErrors() {
            return errors;
        }
    }
}
