package com.example.compilers_laba1;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import parser.ast.BinaryOpNode;
import parser.ast.ExpressionNode;
import parser.ast.IdentifierNode;
import parser.ast.LambdaDeclarationNode;
import parser.ast.NumberLiteralNode;
import parser.ast.ParameterNode;
import parser.ast.ProgramNode;

import java.util.ArrayList;
import java.util.List;

public final class AstGraphWindow {

    private static final double HORIZONTAL_GAP = 32.0;
    private static final double VERTICAL_GAP = 100.0;
    private static final double NODE_MIN_WIDTH = 170.0;
    private static final double NODE_PADDING_X = 12.0;
    private static final double NODE_PADDING_Y = 10.0;
    private static final double WINDOW_WIDTH = 1100.0;
    private static final double WINDOW_HEIGHT = 720.0;
    private static final Font NODE_FONT = Font.font("Consolas", 13);

    private AstGraphWindow() {
    }

    public static void show(ProgramNode programNode) {
        VisualNode root = buildVisualTree(programNode);
        computeMetrics(root);
        setCoordinates(root, 40.0, 50.0);

        Pane graphPane = new Pane();
        graphPane.setPadding(new Insets(20));
        graphPane.setStyle("-fx-background-color: #fafafa;");

        drawTree(graphPane, root);

        double paneWidth = Math.max(root.subtreeWidth + 80.0, WINDOW_WIDTH);
        double paneHeight = Math.max(maxDepth(root) * VERTICAL_GAP + 180.0, WINDOW_HEIGHT);
        graphPane.setMinSize(paneWidth, paneHeight);
        graphPane.setPrefSize(paneWidth, paneHeight);

        ScrollPane scrollPane = new ScrollPane(graphPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setPannable(true);

        Stage stage = new Stage();
        stage.setTitle("AST Graph");
        stage.setScene(new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    private static VisualNode buildVisualTree(ProgramNode programNode) {
        VisualNode root = new VisualNode("ProgramNode", List.of("declarations: " + programNode.getDeclarations().size()));
        for (LambdaDeclarationNode declaration : programNode.getDeclarations()) {
            root.children.add(buildDeclarationNode(declaration));
        }
        return root;
    }

    private static VisualNode buildDeclarationNode(LambdaDeclarationNode declaration) {
        VisualNode declarationNode = new VisualNode(
                "LambdaDeclNode",
                List.of("name: " + declaration.getName(), "params: " + declaration.getParameters().size())
        );

        VisualNode paramsContainer = new VisualNode("ParametersNode", List.of());
        if (declaration.getParameters().isEmpty()) {
            paramsContainer.children.add(new VisualNode("ParameterNode", List.of("empty")));
        } else {
            for (ParameterNode parameterNode : declaration.getParameters()) {
                paramsContainer.children.add(new VisualNode("ParameterNode", List.of("name: " + parameterNode.getName())));
            }
        }

        VisualNode bodyContainer = new VisualNode("BodyNode", List.of());
        bodyContainer.children.add(buildExpressionNode(declaration.getBody()));

        declarationNode.children.add(paramsContainer);
        declarationNode.children.add(bodyContainer);
        return declarationNode;
    }

    private static VisualNode buildExpressionNode(ExpressionNode expressionNode) {
        if (expressionNode instanceof BinaryOpNode binaryOpNode) {
            VisualNode node = new VisualNode("BinaryOpNode", List.of("op: " + binaryOpNode.getOperator()));
            node.children.add(buildExpressionNode(binaryOpNode.getLeft()));
            node.children.add(buildExpressionNode(binaryOpNode.getRight()));
            return node;
        }

        if (expressionNode instanceof IdentifierNode identifierNode) {
            return new VisualNode("IdentifierNode", List.of("name: " + identifierNode.getName()));
        }

        if (expressionNode instanceof NumberLiteralNode numberLiteralNode) {
            return new VisualNode("IntLiteralNode", List.of("value: " + numberLiteralNode.getLiteral()));
        }

        return new VisualNode("UnknownExpressionNode", List.of());
    }

    private static void computeMetrics(VisualNode node) {
        node.nodeWidth = calculateNodeWidth(node);
        node.nodeHeight = calculateNodeHeight(node);

        if (node.children.isEmpty()) {
            node.subtreeWidth = node.nodeWidth;
            return;
        }

        double childrenWidth = 0.0;
        for (int i = 0; i < node.children.size(); i++) {
            VisualNode child = node.children.get(i);
            computeMetrics(child);
            childrenWidth += child.subtreeWidth;
            if (i < node.children.size() - 1) {
                childrenWidth += HORIZONTAL_GAP;
            }
        }

        node.subtreeWidth = Math.max(node.nodeWidth, childrenWidth);
    }

    private static double calculateNodeWidth(VisualNode node) {
        int maxChars = node.type.length();
        for (String attribute : node.attributes) {
            maxChars = Math.max(maxChars, attribute.length());
        }
        double estimated = maxChars * 7.2 + NODE_PADDING_X * 2;
        return Math.max(NODE_MIN_WIDTH, estimated);
    }

    private static double calculateNodeHeight(VisualNode node) {
        int lines = 1 + node.attributes.size();
        return lines * 18.0 + NODE_PADDING_Y * 2;
    }

    private static void setCoordinates(VisualNode node, double left, double yCenter) {
        node.xCenter = left + node.subtreeWidth / 2.0;
        node.yCenter = yCenter;

        if (node.children.isEmpty()) {
            return;
        }

        double childrenClusterWidth = 0.0;
        for (int i = 0; i < node.children.size(); i++) {
            childrenClusterWidth += node.children.get(i).subtreeWidth;
            if (i < node.children.size() - 1) {
                childrenClusterWidth += HORIZONTAL_GAP;
            }
        }

        double childLeft = left + (node.subtreeWidth - childrenClusterWidth) / 2.0;
        for (VisualNode child : node.children) {
            setCoordinates(child, childLeft, yCenter + VERTICAL_GAP);
            childLeft += child.subtreeWidth + HORIZONTAL_GAP;
        }
    }

    private static void drawTree(Pane pane, VisualNode root) {
        drawEdges(pane, root);
        drawNodes(pane, root);
    }

    private static void drawEdges(Pane pane, VisualNode node) {
        for (VisualNode child : node.children) {
            Line edge = new Line(
                    node.xCenter,
                    node.yCenter + node.nodeHeight / 2.0,
                    child.xCenter,
                    child.yCenter - child.nodeHeight / 2.0
            );
            edge.setStroke(Color.web("#5f6368"));
            edge.setStrokeWidth(1.4);
            pane.getChildren().add(edge);
            drawEdges(pane, child);
        }
    }

    private static void drawNodes(Pane pane, VisualNode node) {
        Rectangle box = new Rectangle(
                node.xCenter - node.nodeWidth / 2.0,
                node.yCenter - node.nodeHeight / 2.0,
                node.nodeWidth,
                node.nodeHeight
        );
        box.setArcWidth(14.0);
        box.setArcHeight(14.0);
        box.setFill(Color.web("#eef6ff"));
        box.setStroke(Color.web("#2f5d8a"));
        box.setStrokeWidth(1.2);
        pane.getChildren().add(box);

        StringBuilder textBuilder = new StringBuilder(node.type);
        for (String attribute : node.attributes) {
            textBuilder.append('\n').append(attribute);
        }

        Text text = new Text(textBuilder.toString());
        text.setFont(NODE_FONT);
        text.setFill(Color.web("#1f2937"));
        text.setX(box.getX() + NODE_PADDING_X);
        text.setY(box.getY() + NODE_PADDING_Y + 14.0);
        pane.getChildren().add(text);

        for (VisualNode child : node.children) {
            drawNodes(pane, child);
        }
    }

    private static int maxDepth(VisualNode node) {
        if (node.children.isEmpty()) {
            return 1;
        }
        int best = 1;
        for (VisualNode child : node.children) {
            best = Math.max(best, 1 + maxDepth(child));
        }
        return best;
    }

    private static final class VisualNode {
        private final String type;
        private final List<String> attributes;
        private final List<VisualNode> children = new ArrayList<>();
        private double nodeWidth;
        private double nodeHeight;
        private double subtreeWidth;
        private double xCenter;
        private double yCenter;

        private VisualNode(String type, List<String> attributes) {
            this.type = type;
            this.attributes = attributes == null ? List.of() : List.copyOf(attributes);
        }
    }
}
