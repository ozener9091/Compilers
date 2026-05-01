package parser.ir;

import parser.ast.BinaryOpNode;
import parser.ast.ExpressionNode;
import parser.ast.IdentifierNode;
import parser.ast.LambdaDeclarationNode;
import parser.ast.NumberLiteralNode;
import parser.ast.ProgramNode;

import java.util.ArrayList;
import java.util.List;

public final class IntermediateCodeGenerator {

    private IntermediateCodeGenerator() {
    }

    public static ProgramIrResult build(ProgramNode programNode) {
        if (programNode == null || programNode.getDeclarations().isEmpty()) {
            return new ProgramIrResult(List.of());
        }

        List<LambdaIrResult> results = new ArrayList<>();
        for (LambdaDeclarationNode declaration : programNode.getDeclarations()) {
            if (declaration == null || declaration.getBody() == null) {
                continue;
            }

            IrBuildContext context = new IrBuildContext();
            String finalResult = context.emitExpression(declaration.getBody());
            List<String> poliz = context.buildPoliz(declaration.getBody());

            Long evaluatedValue = null;
            String evaluationError = null;
            if (context.isNumberOnly(declaration.getBody())) {
                try {
                    evaluatedValue = context.evaluatePoliz(poliz);
                } catch (ArithmeticException exception) {
                    evaluationError = exception.getMessage();
                }
            } else {
                evaluationError = "Вычисление пропущено: выражение содержит идентификаторы.";
            }

            results.add(new LambdaIrResult(
                    declaration.getName(),
                    finalResult,
                    context.getQuadruples(),
                    poliz,
                    evaluatedValue,
                    evaluationError
            ));
        }

        return new ProgramIrResult(results);
    }

    public static final class ProgramIrResult {
        private final List<LambdaIrResult> declarations;

        public ProgramIrResult(List<LambdaIrResult> declarations) {
            this.declarations = declarations == null ? List.of() : List.copyOf(declarations);
        }

        public List<LambdaIrResult> getDeclarations() {
            return declarations;
        }
    }

    public static final class LambdaIrResult {
        private final String declarationName;
        private final String finalResult;
        private final List<Quadruple> quadruples;
        private final List<String> poliz;
        private final Long evaluatedValue;
        private final String evaluationError;

        public LambdaIrResult(
                String declarationName,
                String finalResult,
                List<Quadruple> quadruples,
                List<String> poliz,
                Long evaluatedValue,
                String evaluationError
        ) {
            this.declarationName = declarationName;
            this.finalResult = finalResult;
            this.quadruples = quadruples == null ? List.of() : List.copyOf(quadruples);
            this.poliz = poliz == null ? List.of() : List.copyOf(poliz);
            this.evaluatedValue = evaluatedValue;
            this.evaluationError = evaluationError;
        }

        public String getDeclarationName() {
            return declarationName;
        }

        public String getFinalResult() {
            return finalResult;
        }

        public List<Quadruple> getQuadruples() {
            return quadruples;
        }

        public List<String> getPoliz() {
            return poliz;
        }

        public Long getEvaluatedValue() {
            return evaluatedValue;
        }

        public String getEvaluationError() {
            return evaluationError;
        }
    }

    public static final class Quadruple {
        private final int index;
        private final String op;
        private final String arg1;
        private final String arg2;
        private final String result;

        public Quadruple(int index, String op, String arg1, String arg2, String result) {
            this.index = index;
            this.op = op;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.result = result;
        }

        public int getIndex() {
            return index;
        }

        public String getOp() {
            return op;
        }

        public String getArg1() {
            return arg1;
        }

        public String getArg2() {
            return arg2;
        }

        public String getResult() {
            return result;
        }
    }

    private static final class IrBuildContext {
        private final List<Quadruple> quadruples = new ArrayList<>();
        private int tempCounter = 1;

        private List<Quadruple> getQuadruples() {
            return List.copyOf(quadruples);
        }

        private String emitExpression(ExpressionNode expressionNode) {
            if (expressionNode instanceof NumberLiteralNode numberLiteralNode) {
                return numberLiteralNode.getLiteral();
            }

            if (expressionNode instanceof IdentifierNode identifierNode) {
                return identifierNode.getName();
            }

            if (expressionNode instanceof BinaryOpNode binaryOpNode) {
                String left = emitExpression(binaryOpNode.getLeft());
                String right = emitExpression(binaryOpNode.getRight());
                String tempResult = nextTemp();
                quadruples.add(new Quadruple(
                        quadruples.size() + 1,
                        binaryOpNode.getOperator(),
                        left,
                        right,
                        tempResult
                ));
                return tempResult;
            }

            return "";
        }

        private List<String> buildPoliz(ExpressionNode expressionNode) {
            List<String> output = new ArrayList<>();
            buildPolizRec(expressionNode, output);
            return output;
        }

        private void buildPolizRec(ExpressionNode expressionNode, List<String> output) {
            if (expressionNode instanceof NumberLiteralNode numberLiteralNode) {
                output.add(numberLiteralNode.getLiteral());
                return;
            }

            if (expressionNode instanceof IdentifierNode identifierNode) {
                output.add(identifierNode.getName());
                return;
            }

            if (expressionNode instanceof BinaryOpNode binaryOpNode) {
                buildPolizRec(binaryOpNode.getLeft(), output);
                buildPolizRec(binaryOpNode.getRight(), output);
                output.add(binaryOpNode.getOperator());
            }
        }

        private boolean isNumberOnly(ExpressionNode expressionNode) {
            if (expressionNode instanceof NumberLiteralNode) {
                return true;
            }

            if (expressionNode instanceof IdentifierNode) {
                return false;
            }

            if (expressionNode instanceof BinaryOpNode binaryOpNode) {
                return isNumberOnly(binaryOpNode.getLeft()) && isNumberOnly(binaryOpNode.getRight());
            }

            return false;
        }

        private long evaluatePoliz(List<String> poliz) {
            List<Long> stack = new ArrayList<>();

            for (String token : poliz) {
                if (isOperator(token)) {
                    if (stack.size() < 2) {
                        throw new ArithmeticException("Некорректный ПОЛИЗ: недостаточно операндов.");
                    }

                    long right = stack.removeLast();
                    long left = stack.removeLast();
                    long value = applyOperator(token, left, right);
                    stack.add(value);
                    continue;
                }

                try {
                    stack.add(Long.parseLong(token));
                } catch (NumberFormatException exception) {
                    throw new ArithmeticException("Вычисление возможно только для целочисленного выражения.");
                }
            }

            if (stack.size() != 1) {
                throw new ArithmeticException("Некорректный ПОЛИЗ: лишние элементы в стеке.");
            }

            return stack.getFirst();
        }

        private long applyOperator(String operator, long left, long right) {
            return switch (operator) {
                case "+" -> Math.addExact(left, right);
                case "-" -> Math.subtractExact(left, right);
                case "*" -> Math.multiplyExact(left, right);
                case "/" -> {
                    if (right == 0) {
                        throw new ArithmeticException("Деление на ноль.");
                    }
                    yield left / right;
                }
                case "//" -> {
                    if (right == 0) {
                        throw new ArithmeticException("Деление на ноль.");
                    }
                    yield Math.floorDiv(left, right);
                }
                case "%" -> {
                    if (right == 0) {
                        throw new ArithmeticException("Деление на ноль.");
                    }
                    yield left % right;
                }
                case "**" -> powExact(left, right);
                default -> throw new ArithmeticException("Неизвестный оператор: " + operator);
            };
        }

        private long powExact(long base, long exponent) {
            if (exponent < 0) {
                throw new ArithmeticException("Отрицательная степень для целочисленного вычисления не поддерживается.");
            }

            long result = 1L;
            for (long i = 0; i < exponent; i++) {
                result = Math.multiplyExact(result, base);
            }
            return result;
        }

        private boolean isOperator(String token) {
            return "+".equals(token)
                    || "-".equals(token)
                    || "*".equals(token)
                    || "/".equals(token)
                    || "//".equals(token)
                    || "%".equals(token)
                    || "**".equals(token);
        }

        private String nextTemp() {
            return "t" + tempCounter++;
        }
    }
}
