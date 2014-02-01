package brainslug.flow.model.expression;

public class TrueDefinition<V> extends EqualDefinition<V, Boolean> {
  public TrueDefinition(V value) {
    super(value, true);
  }
}
