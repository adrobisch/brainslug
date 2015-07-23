package brainslug.flow.execution.node;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.path.ThenDefinition;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceDefinition> {

  private ExpressionEvaluator expressionEvaluator;

  public ChoiceNodeExecutor(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  @Override
  public FlowNodeExecutionResult execute(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (expressionEvaluator.evaluate(thenPath.getExpression(), execution, Boolean.class)) {
        return new FlowNodeExecutionResult(choiceDefinition).withNext(thenPath.getFirstNode());
      }
    }

    return tryOtherwise(choiceDefinition, execution);
  }

  FlowNodeExecutionResult tryOtherwise(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    if (choiceDefinition.getOtherwisePath().isPresent()) {
      return new FlowNodeExecutionResult(choiceDefinition).withNext(choiceDefinition.getOtherwisePath().get().getFirstNode());
    } else {
      throw new IllegalStateException("no choice path was eligible for execution and no default path was set. " + choiceDefinition + ", trigger: " + execution.getTrigger());
    }
  }

}
