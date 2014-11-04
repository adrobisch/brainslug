package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.marker.IntermediateEvent;

import java.util.Date;

public class EventNodeExecutor extends DefaultNodeExecutor<EventDefinition> {
  @Override
  public FlowNodeExecutionResult execute(EventDefinition eventDefinition, ExecutionContext execution) {
    if (eventDefinition.getContinuePredicate().isPresent() &&
      predicateIsFulfilled(eventDefinition.getContinuePredicate().get(), execution)) {
      return takeAll(eventDefinition);
    } else if (waitingForSignal(eventDefinition, execution.getTrigger())) {
      addTimersIfDefined(eventDefinition, execution);

      return takeNone();
    } else {
      return takeAll(eventDefinition);
    }
  }

  protected void addTimersIfDefined(EventDefinition eventDefinition, ExecutionContext execution) {
    if (eventDefinition.getElapsedTimeDefinition().isPresent()) {
      execution.getBrainslugContext().getAsyncTriggerStore().storeTrigger(
        new AsyncTrigger()
          .withNodeId(eventDefinition.getId())
          .withDefinitionId(execution.getTrigger().getDefinitionId())
          .withInstanceId(execution.getTrigger().getInstanceId())
          .withDueDate(getElapsedTimeDueDate(eventDefinition))
      );
    }
  }

  long getElapsedTimeDueDate(EventDefinition eventDefinition) {
    EventDefinition.TimerDefinition timerDefinition = eventDefinition.getElapsedTimeDefinition().get();
    return getCurrentTime() + timerDefinition.getUnit().toMillis(timerDefinition.getDuration());
  }

  long getCurrentTime() {
    return new Date().getTime();
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
