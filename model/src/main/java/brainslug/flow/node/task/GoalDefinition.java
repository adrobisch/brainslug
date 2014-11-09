package brainslug.flow.node.task;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.expression.PredicateDefinition;

public class GoalDefinition {
  FlowDefinition flowDefinition;
  Identifier id;

  public GoalDefinition(FlowDefinition flowDefinition) {
    this.flowDefinition = flowDefinition;
  }

  public GoalDefinition check(PredicateDefinition predicate) {
    flowDefinition.addGoal(id, predicate);
    return this;
  }

  public GoalDefinition id(Identifier id) {
    this.id = id;
    return this;
  }

  public Identifier getId() {
    return id;
  }
}
