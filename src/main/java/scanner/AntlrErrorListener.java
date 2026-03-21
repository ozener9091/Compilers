package scanner;

import antlr.LambdaLexer;
import antlr.LambdaParserBaseListener;
import antlr.LambdaParser;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.CommonToken;

import java.util.ArrayList;
import java.util.List;

public class AntlrErrorListener extends LambdaParserBaseListener {
    private final List<Scanner.ErrorInfo> errors = new ArrayList<>();

    @Override
    public void visitErrorNode(ErrorNode node) {
        String text = node.getText();
        int line = node.getSymbol().getLine();
        int column = node.getSymbol().getCharPositionInLine() + 1;
        
        errors.add(new Scanner.ErrorInfo(
                "Синтаксическая ошибка",
                text,
                "Неожиданный токен '" + text + "'",
                line,
                column,
                text.length()
        ));
    }

    @Override
    public void enterLambdaExpression(LambdaParser.LambdaExpressionContext ctx) {
        // Проверяем, есть ли ключевое слово LAMBDA
        if (ctx.LAMBDA() == null) {
            int line = ctx.getStart().getLine();
            int column = ctx.getStart().getCharPositionInLine() + 1;
            
            errors.add(new Scanner.ErrorInfo(
                    "Синтаксическая ошибка",
                    "lambda",
                    "Ожидалось ключевое слово 'lambda' перед параметрами.",
                    line,
                    column,
                    0
            ));
        }
    }

    @Override
    public void exitParameterList(LambdaParser.ParameterListContext ctx) {
        // Проверяем лишние запятые в конце списка параметров
        if (ctx.COMMA() != null && ctx.COMMA().size() > 0) {
            // Проверяем, есть ли запятая в конце без последующего идентификатора
            var commas = ctx.COMMA();
            var parameters = ctx.parameter();
            
            if (commas.size() > parameters.size() - 1) {
                // Есть лишняя запятая
                var lastComma = commas.get(commas.size() - 1);
                int line = lastComma.getSymbol().getLine();
                int column = lastComma.getSymbol().getCharPositionInLine() + 1;
                
                errors.add(new Scanner.ErrorInfo(
                        "Синтаксическая ошибка",
                        ",",
                        "Лишняя запятая в списке параметров.",
                        line,
                        column,
                        1
                ));
            }
        }
    }

    public List<Scanner.ErrorInfo> getErrors() {
        return errors;
    }
}
