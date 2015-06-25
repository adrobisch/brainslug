package brainslug.flow.expression;

public class ExpressionBuilder<T extends Expression> {

  T value;

  public ExpressionBuilder(T value) {
    this.value = value;
  }

  public <A> EqualsExpression<T, Value<A>> isEqualTo(final A expectedValue) {
    return new EqualsExpression<T, Value<A>>(value, new Value<A>(expectedValue));
  }

  public EqualsTrueExpression<T> isTrue() {
    return new EqualsTrueExpression<T>(value);
  }

}
