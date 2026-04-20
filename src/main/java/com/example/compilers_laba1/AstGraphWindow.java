package com.example.compilers_laba1;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import parser.ast.BinaryOpNode;
import parser.ast.ExpressionNode;
import parser.ast.IdentifierNode;
import parser.ast.LambdaDeclarationNode;
import parser.ast.NumberLiteralNode;
import parser.ast.ParameterNode;
import parser.ast.ProgramNode;

public final class AstGraphWindow {

    private AstGraphWindow() {
    }

    public static void show(ProgramNode programNode) {
        TreeItem<String> rootItem = buildProgram(programNode);
        rootItem.setExpanded(true);

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(true);

        BorderPane pane = new BorderPane(treeView);

        Stage stage = new Stage();
        stage.setTitle("AST");
        stage.setScene(new Scene(pane, 800, 600));
        stage.show();
    }

    private static TreeItem<String> buildProgram(ProgramNode programNode) {
        TreeItem<String> root = new TreeItem<>("ProgramNode");
        for (LambdaDeclarationNode declaration : programNode.getDeclarations()) {
            root.getChildren().add(buildDeclaration(declaration));
        }
        return root;
    }

    private static TreeItem<String> buildDeclaration(LambdaDeclarationNode declaration) {
        TreeItem<String> item = new TreeItem<>("LambdaDeclNode [name=" + declaration.getName() + "]");

        TreeItem<String> paramsItem = new TreeItem<>("parameters");
        if (declaration.getParameters().isEmpty()) {
            paramsItem.getChildren().add(new TreeItem<>("(empty)"));
        } else {
            for (ParameterNode parameter : declaration.getParameters()) {
                paramsItem.getChildren().add(new TreeItem<>("ParameterNode [name=" + parameter.getName() + "]"));
            }
        }

        TreeItem<String> bodyItem = new TreeItem<>("body");
        bodyItem.getChildren().add(buildExpression(declaration.getBody()));

        item.getChildren().add(paramsItem);
        item.getChildren().add(bodyItem);
        item.setExpanded(true);
        return item;
    }

    private static TreeItem<String> buildExpression(ExpressionNode node) {
        if (node instanceof BinaryOpNode opNode) {
            TreeItem<String> item = new TreeItem<>("BinaryOpNode [op=" + opNode.getOperator() + "]");
            item.getChildren().add(buildExpression(opNode.getLeft()));
            item.getChildren().add(buildExpression(opNode.getRight()));
            item.setExpanded(true);
            return item;
        }

        if (node instanceof IdentifierNode identifierNode) {
            return new TreeItem<>("IdentifierNode [name=" + identifierNode.getName() + "]");
        }

        if (node instanceof NumberLiteralNode literalNode) {
            return new TreeItem<>("IntLiteralNode [value=" + literalNode.getLiteral() + "]");
        }

        return new TreeItem<>("UnknownExpressionNode");
    }
}
