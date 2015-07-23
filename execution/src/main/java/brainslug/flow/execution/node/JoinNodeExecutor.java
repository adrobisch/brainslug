package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.flow.node.JoinDefinition;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.List;

public class JoinNodeExecutor extends DefaultNodeExecutor<JoinDefinition> {

  @Override
  public FlowNodeExecutionResult execute(JoinDefinition joinDefinition, ExecutionContext execution) {
    List<FlowInstanceToken> joinTokens = execution
      .getInstance()
      .getTokens()
      .getNodeTokens(joinDefinition.getId());

    FlowNodeExecutionResult takeAll = takeAll(joinDefinition);

    for (FlowEdgeDefinition edge : joinDefinition.getIncoming()) {
      List<FlowInstanceToken> edgeTokens = getEdgeTokens(edge, joinTokens);

      if (edgeTokens.isEmpty()) {
        return takeNone(joinDefinition, execution.getInstance());
      } else {
        takeAll.withRemovedTokens(joinDefinition.getId(), Option.of(edge.getSource().getId()), 1);
      }
    }

    return takeAll;
  }

  private List<FlowInstanceToken> getEdgeTokens(FlowEdgeDefinition edge, List<FlowInstanceToken> joinTokens) {
    List<FlowInstanceToken> edgeTokens = new ArrayList();
    for (FlowInstanceToken joinToken : joinTokens) {
      if (joinToken.getSourceNodeId().isPresent() && joinToken.getSourceNodeId().get().equals(edge.getSource().getId())) {
        edgeTokens.add(joinToken);
      }
    }
    return edgeTokens;
  }

}
