package brainslug.flow.execution.async;

public class AsyncTaskQuery {
  long maxCount;

  public long getMaxCount() {
    return maxCount;
  }

  public AsyncTaskQuery withMaxCount(long maxCount) {
    this.maxCount = maxCount;
    return this;
  }
}
