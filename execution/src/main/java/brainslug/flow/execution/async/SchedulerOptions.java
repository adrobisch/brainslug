package brainslug.flow.execution.async;

import java.util.concurrent.TimeUnit;

public class SchedulerOptions<SelfType extends SchedulerOptions> {
  protected boolean disabled = false;

  protected long scheduleDelay = 0;
  protected long schedulePeriod = 5;
  protected TimeUnit scheduleUnit = TimeUnit.SECONDS;

  public long getSchedulePeriod() {
    return schedulePeriod;
  }

  public SelfType withSchedulePeriod(long schedulePeriod) {
    this.schedulePeriod = schedulePeriod;
    return (SelfType) this;
  }

  public TimeUnit getScheduleUnit() {
    return scheduleUnit;
  }

  public SelfType withScheduleUnit(TimeUnit scheduleUnit) {
    this.scheduleUnit = scheduleUnit;
    return (SelfType) this;
  }

  public long getScheduleDelay() {
    return scheduleDelay;
  }

  public SelfType withScheduleDelay(long scheduleDelay) {
    this.scheduleDelay = scheduleDelay;
    return (SelfType) this;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public SelfType setDisabled(boolean disabled) {
    this.disabled = disabled;
    return (SelfType) this;
  }

  @Override
  public String toString() {
    return "SchedulerOptions{" +
      "disabled=" + disabled +
      ", scheduleDelay=" + scheduleDelay +
      ", schedulePeriod=" + schedulePeriod +
      ", scheduleUnit=" + scheduleUnit +
      '}';
  }
}
