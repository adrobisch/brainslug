package brainslug.flow.node.event;

import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.timer.TimerDefinition;
import brainslug.util.Option;

import java.util.concurrent.TimeUnit;

abstract public class AbstractEventDefinition<Self extends AbstractEventDefinition> extends FlowNodeDefinition<Self> {

  private PredicateExpression continuePredicate;

  private PredicateExpression conditionPredicate;

  private TimerDefinition elapsedTimeDefinition;

  private TimerDefinition conditionPollingTimeDefinition;

  /**
   * sets a predicate which is checked to determine whether the execution
   * should be continued immediately for this event, without waiting
   * for a signaling trigger.
   *
   * @param continuePredicate the predicate to be checked
   * @return the event definition
   */
  public Self continueIf(PredicateExpression continuePredicate) {
    this.continuePredicate = continuePredicate;
    return self();
  }

  /**
   * defines this event as timed event, causing the execution
   * to wait for the given duration. the trigger will be asynchronously.
   *
   * @param duration the duration to be waited before
   * @param unit the unit of the duration
   * @return the event definition
   */
  public Self timePassed(long duration, TimeUnit unit) {
    this.elapsedTimeDefinition = new TimerDefinition(duration, unit);
    return self();
  }

  /**
   * defines this event as conditional event, causing the execution
   * to wait until the condition is fulfilled. the check of
   * the condition will be done asynchronously be the scheduler
   *
   * @param conditionPredicate the condition predicate check
   * @return the event definition with the predicate
   */
  public Self condition(PredicateExpression conditionPredicate) {
    this.conditionPredicate = conditionPredicate;
    return self();
  }

  /**
   * defines the interval in which conditional event predicates are checked
   * while they are not fulfilled. if not set, the event executor may choose
   * a default interval
   *
   * @param duration of the interval
   * @param timeUnit of the interval
   * @return event definition with defined polling interval
   */
  public Self pollingInterval(long duration, TimeUnit timeUnit) {
    this.conditionPollingTimeDefinition = new TimerDefinition(duration, timeUnit);
    return self();
  }

  public Option<PredicateExpression> getContinuePredicate() {
    return Option.of(continuePredicate);
  }

  public Option<TimerDefinition> getElapsedTimeDefinition() {
    return Option.of(elapsedTimeDefinition);
  }

  public Option<PredicateExpression> getConditionPredicate() {
    return Option.of(conditionPredicate);
  }

  public Option<TimerDefinition> getConditionPollingTimeDefinition() {
    return Option.of(conditionPollingTimeDefinition);
  }
}
