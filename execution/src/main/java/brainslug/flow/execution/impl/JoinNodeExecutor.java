package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.Token;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.JoinDefinition;

import java.util.List;
import java.util.Map;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinDefinition> {

  @Override
  public void postExecute(ExecutionContext context) {
    // do not remove source token in join execution
  }

  @Override
  public List<FlowNodeDefinition> execute(JoinDefinition joinDefinition, ExecutionContext context) {
    Map<Identifier, List<Token>> joinTokens = tokenStore.getTokens(joinDefinition.getId(), context.getTrigger().getInstanceId());
    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<Token> edgeTokens = joinTokens.get(edge.getSource().getId());
      if (edgeTokens == null || edgeTokens.isEmpty()) {
        return takeNone();
      }
    }
    return takeAll(joinDefinition);
  }
}
