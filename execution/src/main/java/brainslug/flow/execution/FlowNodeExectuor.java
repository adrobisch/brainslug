package brainslug.flow.execution;

import brainslug.flow.model.FlowNodeDefinition;

import java.util.List;

public interface FlowNodeExectuor<T extends FlowNodeDefinition> {
  List<FlowNodeDefinition> execute(T nodeDefinition, ExecutionContext context);

  void preExecute(ExecutionContext executionContext);

  void postExecute(ExecutionContext executionContext);
}
