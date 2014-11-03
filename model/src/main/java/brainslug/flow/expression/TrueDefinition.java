package brainslug.flow.expression;

public class TrueDefinition<V> extends EqualDefinition<V, Boolean> {
  public TrueDefinition(V actual) {
    super(actual, true);
  }
}
