package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.path.ThenDefinition;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceDefinition> {

  @Override
  public FlowNodeExecutionResult execute(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();
    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (execution.getBrainslugContext().getPredicateEvaluator().evaluate(thenPath.getPredicateDefinition(), execution)) {
        next.add(thenPath.getPathNodes().peekFirst());
        return new FlowNodeExecutionResult(next);
      }
    }
    return new FlowNodeExecutionResult(next);
  }
}
