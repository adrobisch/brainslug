package brainslug.flow.execution.token;

import brainslug.flow.execution.*;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExecutor<T> {

  protected TokenStore tokenStore;

  public DefaultNodeExecutor<T> withTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    return this;
  }

  @Override
  public FlowNodeExecutionResult execute(T node, ExecutionContext execution) {
    consumeAllNodeTokens(execution.getTrigger().getInstanceId(), execution.getTrigger().getNodeId());
    return takeAll(node);
  }

  protected void consumeAllNodeTokens(Identifier instanceId, Identifier nodeId) {
    Map<Identifier, List<Token>> nodeTokens = tokenStore.getNodeTokens(nodeId, instanceId).groupedBySourceNode();

    for (List<Token> tokens : nodeTokens.values()) {
      removeTokens(instanceId, tokens);
    }
  }

  protected void removeTokens(Identifier instanceId, List<Token> tokens) {
    for (Token token : tokens) {
      tokenStore.removeToken(instanceId, token.getId());
    }
  }

  protected FlowNodeExecutionResult takeAll(FlowNodeDefinition<?> node) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();

    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      next.add(edge.getTarget());
    }

    return new FlowNodeExecutionResult(next);
  }

  protected FlowNodeExecutionResult takeNone() {
    return new FlowNodeExecutionResult(new ArrayList<FlowNodeDefinition>());
  }
}
