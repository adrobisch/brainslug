package brainslug.flow.execution;

import brainslug.flow.node.task.Task;

public abstract class SimpleTask implements Task {
  public abstract void execute(ExecutionContext context);
}
