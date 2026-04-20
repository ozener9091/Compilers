package parser.ast;

import java.util.ArrayList;
import java.util.List;

public final class AstTextPrinter {

    private AstTextPrinter() {
    }

    public static String print(ProgramNode root) {
        if (root == null || root.getDeclarations().isEmpty()) {
            return "AST empty.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getNodeLabel(root)).append('\n');

        List<Object> children = getChildren(root);
        for (int i = 0; i < children.size(); i++) {
            appendChild(builder, children.get(i), "", i == children.size() - 1);
        }

        return builder.toString();
    }

    private static void appendChild(StringBuilder builder, Object child, String prefix, boolean isLast) {
        if (child instanceof AstNode astNode) {
            builder.append(prefix).append(isLast ? "└── " : "├── ").append(getNodeLabel(astNode)).append('\n');
            List<Object> nested = getChildren(astNode);
            String nestedPrefix = prefix + (isLast ? "    " : "│   ");
            for (int i = 0; i < nested.size(); i++) {
                appendChild(builder, nested.get(i), nestedPrefix, i == nested.size() - 1);
            }
            return;
        }

        builder.append(prefix).append(isLast ? "└── " : "├── ").append(child).append('\n');
    }

    private static String getNodeLabel(AstNode node) {
        return switch (node) {
            case ProgramNode ignored -> "ProgramNode";
            case LambdaDeclarationNode ignored -> "LambdaDeclNode";
            case ParameterNode ignored -> "ParameterNode";
            case BinaryOpNode ignored -> "BinaryOpNode";
            case IdentifierNode ignored -> "IdentifierNode";
            case NumberLiteralNode ignored -> "IntLiteralNode";
            default -> node.getClass().getSimpleName();
        };
    }

    private static List<Object> getChildren(AstNode node) {
        List<Object> children = new ArrayList<>();

        switch (node) {
            case ProgramNode program -> children.addAll(program.getDeclarations());
            case LambdaDeclarationNode lambda -> {
                children.add("name: \"" + lambda.getName() + "\"");
                children.add("parameters:");
                if (lambda.getParameters().isEmpty()) {
                    children.add("  (empty)");
                } else {
                    children.addAll(lambda.getParameters());
                }
                children.add("body:");
                children.add(lambda.getBody());
            }
            case ParameterNode parameter -> children.add("name: \"" + parameter.getName() + "\"");
            case BinaryOpNode op -> {
                children.add("operator: \"" + op.getOperator() + "\"");
                children.add(op.getLeft());
                children.add(op.getRight());
            }
            case IdentifierNode identifier -> children.add("name: \"" + identifier.getName() + "\"");
            case NumberLiteralNode literal -> children.add("value: " + literal.getLiteral());
            default -> {
            }
        }

        return children;
    }
}
