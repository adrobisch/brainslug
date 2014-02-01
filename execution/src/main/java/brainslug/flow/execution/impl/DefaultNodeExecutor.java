package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExectuor;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.execution.TokenStoreAware;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExectuor<T>, TokenStoreAware {
  private TokenStore tokenStore;

  @Override
  public List<FlowNodeDefinition> execute(T node, ExecutionContext context) {
    return takeAll(node);
  }

  @Override
  public void preExecute(ExecutionContext context) {
  }

  @Override
  public void postExecute(ExecutionContext context) {
    removeSourceTokenFromStore(context);
  }

  private void removeSourceTokenFromStore(ExecutionContext context) {
    if (context.getTrigger().getInstanceId() != null) {
      tokenStore.removeToken(context.getTrigger().getInstanceId(),
        context.getTrigger().getNodeId(),
        context.getTrigger().getSourceNodeId());
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
