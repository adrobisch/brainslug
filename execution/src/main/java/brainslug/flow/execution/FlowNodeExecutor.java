package brainslug.flow.execution;

import brainslug.flow.model.FlowNodeDefinition;

import java.util.List;

public interface FlowNodeExecutor<T extends FlowNodeDefinition> {
  List<FlowNodeDefinition> execute(T nodeDefinition, ExecutionContext context);
}
