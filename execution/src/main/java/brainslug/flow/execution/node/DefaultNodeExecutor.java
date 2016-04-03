package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.instance.FlowInstance;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.path.FlowEdgeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExecutor<T> {

  @Override
  public FlowNodeExecutionResult execute(T node, ExecutionContext execution) {
    return takeAllAndRemoveFirst(node, execution.getInstance());
  }

  protected FlowNodeExecutionResult takeAllAndRemoveFirst(FlowNodeDefinition<?> node, FlowInstance flowInstance) {
    return takeAll(node).withFirstIncomingTokensRemoved(flowInstance.getTokens());
  }

  protected FlowNodeExecutionResult takeAll(FlowNodeDefinition<?> node) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();

    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      next.add(edge.getTarget());
    }

    return new FlowNodeExecutionResult(node, next);
  }

  protected FlowNodeExecutionResult takeNone(FlowNodeDefinition<?> node, FlowInstance flowInstance) {
    return new FlowNodeExecutionResult(node, Collections.<FlowNodeDefinition>emptyList());
  }
}
