package brainslug.flow.node.event;

import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.event.timer.TimerDefinition;
import brainslug.util.Option;

import java.util.concurrent.TimeUnit;

abstract public class AbstractEventDefinition<Self extends AbstractEventDefinition> extends FlowNodeDefinition<Self> {

  private PredicateDefinition continuePredicate;

  private TimerDefinition elapsedTimeDefinition;

  public Self continueIf(PredicateDefinition continuePredicate) {
    this.continuePredicate = continuePredicate;
    return (Self) this;
  }

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
