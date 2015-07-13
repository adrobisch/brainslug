package brainslug.flow.execution.node;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.flow.node.JoinDefinition;
import brainslug.flow.path.FlowEdgeDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinNodeExecutor, JoinDefinition> {

  @Override
  public FlowNodeExecutionResult execute(JoinDefinition joinDefinition, ExecutionContext execution) {
    Identifier instanceId = execution.getTrigger().getInstanceId();
    Map<Identifier, List<FlowInstanceToken>> joinTokens = tokenOperations.getNodeTokensGroupedBySource(joinDefinition.getId(), instanceId);
    List<FlowInstanceToken> consumedTokens = new ArrayList<FlowInstanceToken>();
    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<FlowInstanceToken> edgeTokens = joinTokens.get(edge.getSource().getId());
      if (edgeTokens == null || edgeTokens.isEmpty()) {
        return takeNone();
      } else {
        consumedTokens.addAll(edgeTokens);
      }
    }
    tokenOperations.removeTokens(instanceId, consumedTokens);
    return takeAll(joinDefinition);
  }

}
