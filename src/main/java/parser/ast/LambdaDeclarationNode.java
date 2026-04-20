package parser.ast;

import java.util.List;

public final class LambdaDeclarationNode implements AstNode {
    private final String name;
    private final int line;
    private final int column;
    private final List<ParameterNode> parameters;
    private final ExpressionNode body;

    public LambdaDeclarationNode(
            String name,
            int line,
            int column,
            List<ParameterNode> parameters,
            ExpressionNode body
    ) {
        this.name = name;
        this.line = line;
        this.column = column;
        this.parameters = parameters == null ? List.of() : List.copyOf(parameters);
        this.body = body;
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

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public ExpressionNode getBody() {
        return body;
    }
}
