package brainslug.flow.expression;

public class PredicateDefinition<T extends Predicate> extends TrueDefinition<T>{
  public PredicateDefinition(T predicate) {
    super(predicate);
  }
}
