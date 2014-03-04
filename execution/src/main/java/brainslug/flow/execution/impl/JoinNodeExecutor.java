package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.JoinDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinDefinition> {

  private final TokenStore tokenStore;

  public JoinNodeExecutor(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  @Override
  public List<FlowNodeDefinition> execute(JoinDefinition joinDefinition, ExecutionContext execution) {
    Map<Identifier, List<Token>> joinTokens = tokenStore.getTokens(joinDefinition.getId(), execution.getTrigger().getInstanceId());
    List<Token> deadTokens = new ArrayList<Token>();
    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<Token> edgeTokens = joinTokens.get(edge.getSource().getId());
      if (edgeTokens == null || edgeTokens.isEmpty()) {
        return takeNone();
      } else {
        deadTokens.addAll(edgeTokens);
      }
    }
    removeTokens(execution, deadTokens);
    return takeAll(joinDefinition);
  }

}
