package parser.ast;

import java.util.List;

public final class ProgramNode implements AstNode {
    private final List<LambdaDeclarationNode> declarations;

    public ProgramNode(List<LambdaDeclarationNode> declarations) {
        this.declarations = declarations == null ? List.of() : List.copyOf(declarations);
    }

    public List<LambdaDeclarationNode> getDeclarations() {
        return declarations;
    }

    public ProgramNode withDeclarations(List<LambdaDeclarationNode> newDeclarations) {
        return new ProgramNode(newDeclarations);
    }

    @Override
    public int getLine() {
        if (declarations.isEmpty()) {
            return 1;
        }
        return declarations.getFirst().getLine();
    }

    @Override
    public int getColumn() {
        if (declarations.isEmpty()) {
            return 1;
        }
        return declarations.getFirst().getColumn();
    }
}
