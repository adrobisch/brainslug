package brainslug.flow.model.expression;

public class EqualDefinition<V, E> {
  private final V actual;
  E expected;

  public EqualDefinition(V actual, E expectedValue) {
    this.expected = expectedValue;
    this.actual = actual;
  }

  public E getExpected() {
    return expected;
  }

  public V getActual() {
    return actual;
  }

  public E getExpectedValue() {
    return this.expected;
  }

}
