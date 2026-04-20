package parser.semantic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class SymbolTable {

    public enum SymbolType {
        INT,
        LAMBDA
    }

    public record Symbol(String name, SymbolType type, int line, int column) {
    }

    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public boolean declare(String name, SymbolType type, int line, int column) {
        Map<String, Symbol> current = currentScope();
        if (current.containsKey(name)) {
            return false;
        }
        current.put(name, new Symbol(name, type, line, column));
        return true;
    }

    public Symbol lookupCurrentScope(String name) {
        return currentScope().get(name);
    }

    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol symbol = scope.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    private Map<String, Symbol> currentScope() {
        if (scopes.isEmpty()) {
            enterScope();
        }
        return scopes.peek();
    }
}
