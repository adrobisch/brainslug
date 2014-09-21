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
    Map<Identifier, List<Token>> joinTokens = tokenStore.tokensGroupedBySource(joinDefinition.getId(), execution.getTrigger().getInstanceId());
    List<Token> consumedTokens = new ArrayList<Token>();
    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<Token> edgeTokens = joinTokens.get(edge.getSource().getId());
      if (edgeTokens == null || edgeTokens.isEmpty()) {
        return takeNone();
      } else {
        consumedTokens.addAll(edgeTokens);
      }
    }
    removeTokens(execution, consumedTokens);
    return takeAll(joinDefinition);
  }

}
