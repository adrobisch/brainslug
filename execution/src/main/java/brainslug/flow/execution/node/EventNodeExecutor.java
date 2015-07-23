package brainslug.flow.execution.node;

import brainslug.flow.context.TriggerContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.event.IntermediateEvent;
import brainslug.flow.node.event.timer.TimerDefinition;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventNodeExecutor extends DefaultNodeExecutor<EventDefinition> {
  AsyncTriggerStore asyncTriggerStore;
  ExpressionEvaluator expressionEvaluator;

  public EventNodeExecutor(AsyncTriggerStore asyncTriggerStore, ExpressionEvaluator expressionEvaluator) {
    this.asyncTriggerStore = asyncTriggerStore;
    this.expressionEvaluator = expressionEvaluator;
  }

  @Override
  public FlowNodeExecutionResult execute(EventDefinition eventDefinition, ExecutionContext execution) {
    if (shouldContinueImmediately(eventDefinition, execution)) {
      return takeAllAndRemoveFirst(eventDefinition, execution.getInstance());
    } else if (eventDefinition.getConditionPredicate().isPresent()) {
      return executeConditionalEvent(eventDefinition, execution);
    } else if (waitingForSignal(eventDefinition, execution.getTrigger())) {
      addTimersIfDefined(eventDefinition, execution);
      return takeNone(eventDefinition, execution.getInstance());
    } else {
      return takeAllAndRemoveFirst(eventDefinition, execution.getInstance());
    }
  }

  protected boolean shouldContinueImmediately(EventDefinition eventDefinition, ExecutionContext execution) {
    return eventDefinition.getContinuePredicate().isPresent() &&
      predicateIsFulfilled(eventDefinition.getContinuePredicate().get(), execution);
  }

  protected FlowNodeExecutionResult executeConditionalEvent(EventDefinition eventDefinition, ExecutionContext execution) {
    if (execution.getTrigger().isSignaling()) {
      return takeAllAndRemoveFirst(eventDefinition, execution.getInstance());
    } else if (execution.getTrigger().isAsync() && predicateIsFulfilled(eventDefinition.getConditionPredicate().get(), execution)) {
      return takeAllAndRemoveFirst(eventDefinition, execution.getInstance());
    } else {
      createAsyncTrigger(eventDefinition, execution, nextPollingDate(eventDefinition));
      return takeNone(eventDefinition, execution.getInstance());
    }
  }

  private long nextPollingDate(EventDefinition eventDefinition) {
    if (eventDefinition.getConditionPollingTimeDefinition().isPresent()) {
      TimerDefinition pollingTimer = eventDefinition.getConditionPollingTimeDefinition().get();
      return dateAfterDuration(pollingTimer.getDuration(), pollingTimer.getUnit());
    } else {
      return dateAfterDuration(3, TimeUnit.SECONDS);
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
    return dateAfterDuration(timerDefinition.getDuration(), timerDefinition.getUnit());
  }

  private long dateAfterDuration(long duration, TimeUnit unit) {
    return getCurrentTime() + unit.toMillis(duration);
  }

  protected long getCurrentTime() {
    return new Date().getTime();
  }

  protected boolean waitingForSignal(EventDefinition eventDefinition, TriggerContext trigger) {
    return eventDefinition.is(IntermediateEvent.class) && !trigger.isSignaling();
  }

  protected boolean predicateIsFulfilled(PredicateExpression eventPredicate, ExecutionContext execution) {
    return expressionEvaluator.evaluate(eventPredicate, execution, Boolean.class);
  }
}
