package brainslug.flow.execution.impl;

import brainslug.flow.execution.*;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExecutor<T> {

  protected TokenStore tokenStore;

  DefaultNodeExecutor withTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    return this;
  }

  @Override
  public List<FlowNodeDefinition> execute(T node, ExecutionContext execution) {
    consumeAllNodeTokens(execution.getTrigger().getInstanceId(), execution.getTrigger().getNodeId());
    return takeAll(node);
  }

  protected void consumeAllNodeTokens(Identifier instanceId, Identifier nodeId) {
    Map<Identifier, List<Token>> nodeTokens = tokenStore.tokensGroupedBySourceNode(nodeId, instanceId);

    for (List<Token> tokens : nodeTokens.values()) {
      removeTokens(instanceId, tokens);
    }
  }

  protected void removeTokens(Identifier instanceId, List<Token> tokens) {
    for (Token token : tokens) {
      tokenStore.removeToken(instanceId, token.getId());
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
}
