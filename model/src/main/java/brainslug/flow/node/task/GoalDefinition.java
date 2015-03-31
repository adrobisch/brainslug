package brainslug.flow.node.task;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.PredicateDefinition;

public class GoalDefinition {
  Identifier id;
  PredicateDefinition predicate;

  public GoalDefinition() {
  }

  public GoalDefinition check(PredicateDefinition predicate) {
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

  public PredicateDefinition getPredicate() {
    return predicate;
  }
}
