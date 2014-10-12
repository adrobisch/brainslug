package brainslug.flow.execution;

import brainslug.flow.node.task.GoalPredicate;

public interface GoalCondition extends GoalPredicate<ExecutionContext> {
  Boolean isFulfilled(ExecutionContext context);
}
