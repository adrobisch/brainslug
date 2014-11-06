package brainslug.flow.execution.async;


public class AsyncTriggerSchedulerOptions extends SchedulerOptions<AsyncTriggerSchedulerOptions> {
  long maxTaskCount = 50;

  public long getMaxTaskCount() {
    return maxTaskCount;
  }

  public AsyncTriggerSchedulerOptions withMaxTaskCount(long maxTaskCount) {
    this.maxTaskCount = maxTaskCount;
    return this;
  }

  @Override
  public String toString() {
    return super.toString() + "{" +
      "maxTaskCount=" + maxTaskCount +
      '}';
  }
}
