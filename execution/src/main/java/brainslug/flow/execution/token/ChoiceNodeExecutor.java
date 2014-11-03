package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.path.ThenDefinition;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceDefinition> {

  @Override
  public FlowNodeExecutionResult execute(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (execution.getBrainslugContext().getPredicateEvaluator().evaluate(thenPath.getPredicateDefinition(), execution)) {
        return new FlowNodeExecutionResult().withNext(thenPath.getFirstNode());
      }
    }
    return new FlowNodeExecutionResult().withNext(choiceDefinition.getOtherwisePath().getFirstNode());
  }

}
