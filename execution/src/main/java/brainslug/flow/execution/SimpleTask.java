package brainslug.flow.execution;

import brainslug.flow.model.Task;

public abstract class SimpleTask implements Task {
  public abstract void execute(ExecutionContext context);
}
