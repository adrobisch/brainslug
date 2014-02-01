package brainslug.flow.model.expression;

public class PredicateBuilder<T> {

  T value;

  public PredicateBuilder(T value) {
    this.value = value;
  }

  public <A> EqualDefinition<T, Constant<A>> isEqualTo(final A expectedValue) {
    return new EqualDefinition<T, Constant<A>>(value, new Constant<A>(expectedValue));
  }

  public TrueDefinition<T> isTrue() {
    return new TrueDefinition<T>(value);
  }

}
