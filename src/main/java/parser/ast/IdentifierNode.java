package parser.ast;

public final class IdentifierNode implements ExpressionNode {
    private final String name;
    private final int line;
    private final int column;

    public IdentifierNode(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
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
