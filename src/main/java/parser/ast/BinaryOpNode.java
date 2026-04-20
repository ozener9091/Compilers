package parser.ast;

public final class BinaryOpNode implements ExpressionNode {
    private final String operator;
    private final int line;
    private final int column;
    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryOpNode(String operator, int line, int column, ExpressionNode left, ExpressionNode right) {
        this.operator = operator;
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public ExpressionNode getRight() {
        return right;
    }
}
