package brainslug.flow.expression;

public class EqualsTrueExpression<V extends Expression> extends EqualsExpression<V, Value<Boolean>> {
  public EqualsTrueExpression(V actual) {
    super(actual, new Value<Boolean>(true));
  }
}
