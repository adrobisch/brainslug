package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.path.ThenDefinition;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceNodeExecutor, ChoiceDefinition> {

  private PredicateEvaluator predicateEvaluator;

  public ChoiceNodeExecutor(PredicateEvaluator predicateEvaluator) {
    this.predicateEvaluator = predicateEvaluator;
  }

  @Override
  public FlowNodeExecutionResult execute(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    removeIncomingTokens(execution.getTrigger());

    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (predicateEvaluator.evaluate(thenPath.getPredicateDefinition(), execution)) {
        return new FlowNodeExecutionResult().withNext(thenPath.getFirstNode());
      }
    }
    return new FlowNodeExecutionResult().withNext(choiceDefinition.getOtherwisePath().getFirstNode());
  }

}
