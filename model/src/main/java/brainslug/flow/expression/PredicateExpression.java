package brainslug.flow.expression;

public class PredicateExpression<T extends Predicate> extends EqualsTrueExpression<Value<T>> {
  public PredicateExpression(T predicate) {
    super(new Value<T>(predicate));
  }
}
