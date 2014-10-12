package brainslug.flow.execution;

import brainslug.flow.node.FlowNodeDefinition;

public interface FlowNodeExecutor<T extends FlowNodeDefinition> {
  FlowNodeExecutionResult execute(T nodeDefinition, ExecutionContext context);
}
