package brainslug.flow.execution.node.task;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.Task;

public interface ExecutableTask<T extends ExecutionContext> extends Task<T> {
}
