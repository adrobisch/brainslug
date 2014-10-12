package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.node.JoinDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinDefinition> {

  @Override
  public brainslug.flow.execution.FlowNodeExecutionResult execute(JoinDefinition joinDefinition, ExecutionContext execution) {
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
