package brainslug.flow.execution.async;

import java.util.concurrent.TimeUnit;

public class AsyncTaskSchedulerOptions  {
  boolean disabled = false;

  long scheduleDelay = 0;
  long schedulePeriod = 5;
  TimeUnit scheduleUnit = TimeUnit.SECONDS;

  long maxTaskCount = 50;

  public long getSchedulePeriod() {
    return schedulePeriod;
  }

  public AsyncTaskSchedulerOptions withSchedulePeriod(long schedulePeriod) {
    this.schedulePeriod = schedulePeriod;
    return this;
  }

  public TimeUnit getScheduleUnit() {
    return scheduleUnit;
  }

  public AsyncTaskSchedulerOptions withScheduleUnit(TimeUnit scheduleUnit) {
    this.scheduleUnit = scheduleUnit;
    return this;
  }

  public long getScheduleDelay() {
    return scheduleDelay;
  }

  public AsyncTaskSchedulerOptions withScheduleDelay(long scheduleDelay) {
    this.scheduleDelay = scheduleDelay;
    return this;
  }

  public long getMaxTaskCount() {
    return maxTaskCount;
  }

  public AsyncTaskSchedulerOptions withMaxTaskCount(long maxTaskCount) {
    this.maxTaskCount = maxTaskCount;
    return this;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public AsyncTaskSchedulerOptions setDisabled(boolean disabled) {
    this.disabled = disabled;
    return this;
  }

  @Override
  public String toString() {
    return "AsyncTaskSchedulerOptions{" +
      "disabled=" + disabled +
      ", scheduleDelay=" + scheduleDelay +
      ", schedulePeriod=" + schedulePeriod +
      ", scheduleUnit=" + scheduleUnit +
      ", maxTaskCount=" + maxTaskCount +
      '}';
  }
}
