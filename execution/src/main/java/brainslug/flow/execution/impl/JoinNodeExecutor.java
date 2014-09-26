package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.Token;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.JoinDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinDefinition> {

  @Override
  public List<FlowNodeDefinition> execute(JoinDefinition joinDefinition, ExecutionContext execution) {
    Identifier instanceId = execution.getTrigger().getInstanceId();
    Map<Identifier, List<Token>> joinTokens = tokenStore.getNodeTokens(joinDefinition.getId(), instanceId).groupedBySourceNode();
    List<Token> consumedTokens = new ArrayList<Token>();
    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<Token> edgeTokens = joinTokens.get(edge.getSource().getId());
      if (edgeTokens == null || edgeTokens.isEmpty()) {
        return takeNone();
      } else {
        consumedTokens.addAll(edgeTokens);
      }
    }
    removeTokens(instanceId, consumedTokens);
    return takeAll(joinDefinition);
  }

}
