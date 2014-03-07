package brainslug.flow.execution.impl;

import brainslug.flow.execution.*;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExectuor<T>, TokenStoreAware {

  protected TokenStore tokenStore;

  @Override
  public List<FlowNodeDefinition> execute(T node, ExecutionContext execution) {
    removeTriggerToken(execution);
    return takeAll(node);
  }

  protected void removeTriggerToken(ExecutionContext execution) {
    if (execution.getTrigger().getInstanceId() != null) {
      tokenStore.removeToken(execution.getTrigger().getInstanceId(),
        execution.getTrigger().getNodeId(),
        new Token(execution.getTrigger().getSourceNodeId()));
    }
  }

  protected void removeTokens(ExecutionContext execution, List<Token> tokens) {
    for (Token token : tokens) {
      tokenStore.removeToken(execution.getTrigger().getInstanceId(),
        execution.getTrigger().getNodeId(),
        token);
    }
  }

  protected List<FlowNodeDefinition> takeAll(FlowNodeDefinition<?> node) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();

    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      next.add(edge.getTarget());
    }

    return next;
  }

  protected List<FlowNodeDefinition> takeNone() {
    return new ArrayList<FlowNodeDefinition>();
  }

  @Override
  public void setTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }
}
