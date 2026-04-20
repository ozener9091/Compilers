package parser.ast;

public sealed interface ExpressionNode extends AstNode permits BinaryOpNode, IdentifierNode, NumberLiteralNode {
}
