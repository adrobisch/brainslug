package brainslug.flow.node.task;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.PredicateExpression;

public class GoalDefinition {
  Identifier id;
  PredicateExpression predicate;

  public GoalDefinition() {
  }

  public GoalDefinition check(PredicateExpression predicate) {
    this.predicate = predicate;
    return this;
  }

  public GoalDefinition id(Identifier id) {
    this.id = id;
    return this;
  }

  public Identifier getId() {
    return id;
  }

  public PredicateExpression getPredicate() {
    return predicate;
  }
}
