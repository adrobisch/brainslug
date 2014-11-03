package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.marker.IntermediateEvent;

public class EventNodeExecutor extends DefaultNodeExecutor<EventDefinition> {
  @Override
  public FlowNodeExecutionResult execute(EventDefinition eventDefinition, ExecutionContext execution) {
    // intermediate events need a signaling trigger
    if (waitingForSignal(eventDefinition, execution.getTrigger())) {
      return takeNone();
    } else if (!eventDefinition.getPredicateDefinition().isPresent()) {
      return takeAll(eventDefinition);
    } else if (predicateIsFulfilled(eventDefinition.getPredicateDefinition().get(), execution)){
      return takeAll(eventDefinition);
    } else { // predicate present and not fulfilled
      return takeNone();
    }
  }

  protected boolean waitingForSignal(EventDefinition eventDefinition, TriggerContext triggerContext) {
    return eventDefinition.hasMixin(IntermediateEvent.class) && !triggerContext.isSignaling();
  }

  protected boolean predicateIsFulfilled(PredicateDefinition eventPredicate, ExecutionContext execution) {
    return execution.getBrainslugContext()
      .getPredicateEvaluator()
      .evaluate(eventPredicate, execution);
  }
}
