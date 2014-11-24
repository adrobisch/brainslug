package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.task.RetryStrategy;

import java.util.Date;
import java.util.concurrent.Callable;

public class ExecuteTaskCallable implements Callable<AsyncTriggerExecutionResult> {
  AsyncTriggerStore asyncTriggerStore;
  RetryStrategy retryStrategy;
  AsyncTriggerExecutor asyncTriggerExecutor;
  BrainslugContext context;
  AsyncTrigger asyncTrigger;

  public ExecuteTaskCallable(BrainslugContext context, AsyncTrigger asyncTrigger, AsyncTriggerStore asyncTriggerStore, AsyncTriggerExecutor asyncTriggerExecutor, RetryStrategy retryStrategy) {
    this.context = context;
    this.asyncTrigger = asyncTrigger;
    this.asyncTriggerStore = asyncTriggerStore;
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    this.retryStrategy = retryStrategy;
  }

  @Override
  public AsyncTriggerExecutionResult call() {
    if (asyncTrigger.getRetries() >= asyncTrigger.getMaxRetries()) {
      throw new IllegalStateException();
    }

    return asyncTriggerExecutor.execute(asyncTrigger, retryStrategy, context, asyncTriggerStore);
  }

  @Override
  public String toString() {
    return "ExecuteTaskCallable{" +
      "retryStrategy=" + retryStrategy +
      ", asyncTrigger=" + asyncTrigger +
      '}';
  }
}
