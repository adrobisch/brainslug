package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.FlowNodeDefinition;

public interface FlowNodeExecutor<T extends FlowNodeDefinition> {
  FlowNodeExecutionResult execute(T nodeDefinition, ExecutionContext context);
}
