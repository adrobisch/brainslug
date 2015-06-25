package brainslug.flow.expression;

public class StringExpression implements Expression {

    final String expressionString;

    public StringExpression(String expressionString) {
        this.expressionString = expressionString;
    }

    @Override
    public String toString() {
        return expressionString;
    }
}
