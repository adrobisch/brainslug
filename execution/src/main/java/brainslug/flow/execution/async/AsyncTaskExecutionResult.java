package brainslug.flow.execution.async;

import brainslug.util.Option;

public class AsyncTaskExecutionResult {
  boolean failed = false;
  Exception exception = null;

  AsyncTaskExecutionResult() {
  }

  public boolean isFailed() {
    return failed;
  }

  public AsyncTaskExecutionResult setFailed(boolean failed) {
    this.failed = failed;
    return this;
  }

  public Option<Exception> getException() {
    return Option.of(exception);
  }

  public AsyncTaskExecutionResult withException(Exception exception) {
    this.exception = exception;
    return this;
  }
}
