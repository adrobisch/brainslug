package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.TriggerContext;
import brainslug.flow.execution.token.TokenOperations;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.path.FlowEdgeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultNodeExecutor<SelfType, T extends FlowNodeDefinition> implements FlowNodeExecutor<T> {

  protected TokenOperations tokenOperations;

  public SelfType withTokenOperations(TokenOperations tokenOperations) {
    this.tokenOperations = tokenOperations;
    return (SelfType) this;
  }

  @Override
  public FlowNodeExecutionResult execute(T node, ExecutionContext execution) {
    removeIncomingTokens(execution.getTrigger());
    return takeAll(node);
  }

  protected void removeIncomingTokens(TriggerContext trigger) {
    tokenOperations.removeFirstIncomingTokens(trigger.getNodeId(), trigger.getInstanceId());
  }

  protected FlowNodeExecutionResult takeAll(FlowNodeDefinition<?> node) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();

    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      next.add(edge.getTarget());
    }

    return new FlowNodeExecutionResult(next);
  }

  protected FlowNodeExecutionResult takeNone() {
    return new FlowNodeExecutionResult(Collections.<FlowNodeDefinition>emptyList());
  }
}
