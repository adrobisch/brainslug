package brainslug.flow.node.task;

import brainslug.flow.context.ExecutionContext;

public interface Task<T extends ExecutionContext> {
  public void execute(T taskContext);
}
