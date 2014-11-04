package brainslug.flow.execution.async;

import brainslug.util.Option;

import java.util.Date;

public class AsyncTriggerQuery {
  long maxCount = 100;
  Date overdueDate;

  public long getMaxCount() {
    return maxCount;
  }

  public AsyncTriggerQuery withMaxCount(long maxCount) {
    this.maxCount = maxCount;
    return this;
  }

  public Option<Date> getOverdueDate() {
    return Option.of(overdueDate);
  }

  public AsyncTriggerQuery withOverdueDate(Date overdueDate) {
    this.overdueDate = overdueDate;
    return this;
  }
}
