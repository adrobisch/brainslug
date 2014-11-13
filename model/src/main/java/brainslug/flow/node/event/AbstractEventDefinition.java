package brainslug.flow.node.event;

import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.timer.TimerDefinition;
import brainslug.util.Option;

import java.util.concurrent.TimeUnit;

abstract public class AbstractEventDefinition<Self extends AbstractEventDefinition> extends FlowNodeDefinition<Self> {

  private PredicateDefinition continuePredicate;

  private TimerDefinition elapsedTimeDefinition;

  /**
   * sets a predicate which is checked to determine whether the execution
   * should be continued immediately for this event, without waiting
   * for a signaling trigger.
   *
   * @param continuePredicate the predicate to be checked
   * @return the event definition
   */
  public Self continueIf(PredicateDefinition continuePredicate) {
    this.continuePredicate = continuePredicate;
    return (Self) this;
  }

  /**
   * defined this event as timed event, causing the execution
   * to wait for the given duration. the trigger will be asynchronously.
   *
   * @param duration the duration to be waited before
   * @param unit the unit of the duration
   * @return the event definition
   */
  public Self elapsedTime(long duration, TimeUnit unit) {
    this.elapsedTimeDefinition = new TimerDefinition(duration, unit);
    return (Self) this;
  }

  public Option<PredicateDefinition> getContinuePredicate() {
    return Option.of(continuePredicate);
  }

  public Option<TimerDefinition> getElapsedTimeDefinition() {
    return Option.of(elapsedTimeDefinition);
  }

}
