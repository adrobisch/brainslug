package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.ChoiceDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.ThenDefinition;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNodeExecutor extends DefaultNodeExecutor<ChoiceDefinition> {

  @Override
  public List<FlowNodeDefinition> execute(ChoiceDefinition choiceDefinition, ExecutionContext context) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();
    for (ThenDefinition thenPath : choiceDefinition.getThenPaths()) {
      if (context.getBrainslugContext().getPredicateEvaluator().evaluate(thenPath.getPredicateDefinition())) {
        next.add(thenPath.getPathNodes().getFirst());
        return next;
      }
    }
    return next;
  }
}
