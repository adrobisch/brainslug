package brainslug.flow.execution;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.Task;

public interface SimpleTask extends Task<ExecutionContext> {
  public void execute(ExecutionContext context);
}
