package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.ChoiceDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.ThenDefinition;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceDefinition> {

  @Override
  public List<FlowNodeDefinition> execute(ChoiceDefinition choiceDefinition, ExecutionContext execution) {
    removeTriggerToken(execution);

    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();
    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (execution.getBrainslugContext().getPredicateEvaluator().evaluate(thenPath.getPredicateDefinition(), execution)) {
        next.add(thenPath.getPathNodes().get(1));
        return next;
      }
    }
    return next;
  }
}
