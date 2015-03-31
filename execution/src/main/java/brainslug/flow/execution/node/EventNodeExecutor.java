package brainslug.flow.execution.node;

import brainslug.flow.context.TriggerContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.event.IntermediateEvent;
import brainslug.flow.node.event.timer.TimerDefinition;

import java.util.Date;

public class EventNodeExecutor extends DefaultNodeExecutor<EventNodeExecutor, EventDefinition> {
  AsyncTriggerStore asyncTriggerStore;
  PredicateEvaluator predicateEvaluator;

  public EventNodeExecutor(AsyncTriggerStore asyncTriggerStore, PredicateEvaluator predicateEvaluator) {
    this.asyncTriggerStore = asyncTriggerStore;
    this.predicateEvaluator = predicateEvaluator;
  }

  @Override
  public FlowNodeExecutionResult execute(EventDefinition eventDefinition, ExecutionContext execution) {
    removeIncomingTokens(execution.getTrigger());

    if (shouldContinueImmediately(eventDefinition, execution)) {
      return takeAll(eventDefinition);
    } else if (eventDefinition.getConditionPredicate().isPresent()) {
      return executeConditionalEvent(eventDefinition, execution);
    } else if (waitingForSignal(eventDefinition, execution.getTrigger())) {
      addTimersIfDefined(eventDefinition, execution);
      return takeNone();
    } else {
      return takeAll(eventDefinition);
    }
  }

  protected boolean shouldContinueImmediately(EventDefinition eventDefinition, ExecutionContext execution) {
    return eventDefinition.getContinuePredicate().isPresent() &&
      predicateIsFulfilled(eventDefinition.getContinuePredicate().get(), execution);
  }

  protected FlowNodeExecutionResult executeConditionalEvent(EventDefinition eventDefinition, ExecutionContext execution) {
    if (execution.getTrigger().isSignaling()) {
      return takeAll(eventDefinition);
    } else if (!execution.getTrigger().isAsync()){
      createAsyncTrigger(eventDefinition, execution, 0);
      return takeNone();
    } else if (predicateIsFulfilled(eventDefinition.getConditionPredicate().get(), execution)) {
      return takeAll(eventDefinition);
    } else {
      return takeNone();
    }
  }

  protected void addTimersIfDefined(EventDefinition eventDefinition, ExecutionContext execution) {
    if (eventDefinition.getElapsedTimeDefinition().isPresent()) {
      createAsyncTrigger(eventDefinition, execution, getElapsedTimeDueDate(eventDefinition));
    }
  }

  protected void createAsyncTrigger(EventDefinition eventDefinition, ExecutionContext execution, long dueDate) {
    asyncTriggerStore.storeTrigger(
      new AsyncTrigger()
        .withNodeId(eventDefinition.getId())
        .withDefinitionId(execution.getTrigger().getDefinitionId())
        .withInstanceId(execution.getTrigger().getInstanceId())
        .withDueDate(dueDate)
    );
  }

  long getElapsedTimeDueDate(EventDefinition eventDefinition) {
    TimerDefinition timerDefinition = eventDefinition.getElapsedTimeDefinition().get();
    return getCurrentTime() + timerDefinition.getUnit().toMillis(timerDefinition.getDuration());
  }

  protected long getCurrentTime() {
    return new Date().getTime();
  }

  protected boolean waitingForSignal(EventDefinition eventDefinition, TriggerContext trigger) {
    return eventDefinition.is(IntermediateEvent.class) && !trigger.isSignaling();
  }

  protected boolean predicateIsFulfilled(PredicateDefinition eventPredicate, ExecutionContext execution) {
    return predicateEvaluator
      .evaluate(eventPredicate, execution);
  }
}
