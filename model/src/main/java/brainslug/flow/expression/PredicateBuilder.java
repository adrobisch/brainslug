package brainslug.flow.expression;

public class PredicateBuilder<T> {

  T value;

  public PredicateBuilder(T value) {
    this.value = value;
  }

  public <A> EqualDefinition<T, Expression<A>> isEqualTo(final A expectedValue) {
    return new EqualDefinition<T, Expression<A>>(value, new Expression<A>(expectedValue));
  }

  public TrueDefinition<T> isTrue() {
    return new TrueDefinition<T>(value);
  }

}
