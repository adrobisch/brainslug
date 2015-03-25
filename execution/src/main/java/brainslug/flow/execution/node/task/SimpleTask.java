package brainslug.flow.execution.node.task;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.Task;

public interface SimpleTask extends Task<ExecutionContext> {
  public void execute(ExecutionContext context);
}