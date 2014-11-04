package brainslug.flow.node;

import brainslug.flow.expression.PredicateDefinition;
import brainslug.util.Option;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventDefinition extends FlowNodeDefinition<EventDefinition> {

  private PredicateDefinition continuePredicate;

  private TimerDefinition elapsedTimeDefinition;
  private Date fixedDate;

  public EventDefinition continueIf(PredicateDefinition continuePredicate) {
    this.continuePredicate = continuePredicate;
    return this;
  }

  public EventDefinition elapsedTime(long duration, TimeUnit unit) {
    this.elapsedTimeDefinition = new TimerDefinition(duration, unit);
    return this;
  }


  public EventDefinition fixedDate(Date fixedDate) {
    this.fixedDate = fixedDate;
    return this;
  }

  public Option<PredicateDefinition> getContinuePredicate() {
    return Option.of(continuePredicate);
  }

  public Option<TimerDefinition> getElapsedTimeDefinition() {
    return Option.of(elapsedTimeDefinition);
  }

  public Option<Date> getFixedDate() {
    return Option.of(fixedDate);
  }

  public static class TimerDefinition {
    final long duration;
    final TimeUnit unit;

    TimerDefinition(long duration, TimeUnit unit) {
      this.duration = duration;
      this.unit = unit;
    }

    public long getDuration() {
      return duration;
    }

    public TimeUnit getUnit() {
      return unit;
    }
  }

}
