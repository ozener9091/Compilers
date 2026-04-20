package parser.ast;

public final class ParameterNode implements AstNode {
    private final String name;
    private final int line;
    private final int column;

    public ParameterNode(String name, int line, int column) {
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
