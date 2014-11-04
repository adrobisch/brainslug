package brainslug.flow.execution.async;

import brainslug.util.Option;

public class AsyncTriggerExecutionResult {
  boolean failed = false;
  Exception exception = null;

  AsyncTriggerExecutionResult() {
  }

  public boolean isFailed() {
    return failed;
  }

  public AsyncTriggerExecutionResult setFailed(boolean failed) {
    this.failed = failed;
    return this;
  }

  public Option<Exception> getException() {
    return Option.of(exception);
  }

  public AsyncTriggerExecutionResult withException(Exception exception) {
    this.exception = exception;
    return this;
  }
}
