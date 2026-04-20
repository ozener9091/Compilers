package parser.ast;

public final class NumberLiteralNode implements ExpressionNode {
    private final String literal;
    private final int line;
    private final int column;

    public NumberLiteralNode(String literal, int line, int column) {
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
