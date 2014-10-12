package brainslug.flow.node.task;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;

public class GoalDefinition {
  FlowDefinition flowDefinition;
  Identifier id;

  public GoalDefinition(FlowDefinition flowDefinition) {
    this.flowDefinition = flowDefinition;
  }

  public GoalDefinition check(GoalPredicate predicate) {
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
