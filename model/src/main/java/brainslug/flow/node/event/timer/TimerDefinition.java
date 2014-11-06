package brainslug.flow.node.event.timer;

import java.util.concurrent.TimeUnit;

public class TimerDefinition {
  final long duration;
  final TimeUnit unit;

  public TimerDefinition(long duration, TimeUnit unit) {
    this.duration = duration;
    this.unit = unit;
  }

  public long getDuration() {
    return duration;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimerDefinition that = (TimerDefinition) o;

    if (duration != that.duration) return false;
    if (unit != that.unit) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (duration ^ (duration >>> 32));
    result = 31 * result + (unit != null ? unit.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TimerDefinition{" +
      "duration=" + duration +
      ", unit=" + unit +
      '}';
  }
}
