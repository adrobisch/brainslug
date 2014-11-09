package brainslug.flow.execution.async;

import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.node.task.RetryStrategy;

import java.util.Date;
import java.util.concurrent.Callable;

public class ExecuteTaskCallable implements Callable<AsyncTriggerExecutionResult> {
  RetryStrategy retryStrategy;
  AsyncTriggerExecutor asyncTriggerExecutor;
  DefaultBrainslugContext context;
  AsyncTrigger asyncTrigger;

  ExecuteTaskCallable(DefaultBrainslugContext context, AsyncTrigger asyncTrigger, AsyncTriggerExecutor asyncTriggerExecutor, RetryStrategy retryStrategy) {
    this.context = context;
    this.asyncTrigger = asyncTrigger;
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    this.retryStrategy = retryStrategy;
  }

  @Override
  public AsyncTriggerExecutionResult call() {
    if (asyncTrigger.getRetries() >= asyncTrigger.getMaxRetries()) {
      throw new IllegalStateException();
    }

    AsyncTriggerExecutionResult execution = asyncTriggerExecutor.execute(asyncTrigger, context);
    if (execution.isFailed()) {
      context.getAsyncTriggerStore().storeTrigger(asyncTrigger
          .incrementRetries()
          .withDueDate(retryStrategy
            .nextRetry(asyncTrigger.getRetries(), getBaseDate()).getTime())
          .withErrorDetails(new AsyncTriggerErrorDetails(execution.getException().get()))
      );
    }
    return execution;
  }

  public Date getBaseDate() {
    return new Date();
  }

  @Override
  public String toString() {
    return "ExecuteTaskCallable{" +
      "retryStrategy=" + retryStrategy +
      ", asyncTrigger=" + asyncTrigger +
      '}';
  }
}
