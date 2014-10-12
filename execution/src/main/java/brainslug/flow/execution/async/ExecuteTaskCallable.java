package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.task.RetryStrategy;

import java.util.Date;
import java.util.concurrent.Callable;

public class ExecuteTaskCallable implements Callable<AsyncTaskExecutionResult> {
  RetryStrategy retryStrategy;
  AsyncTaskExecutor asyncTaskExecutor;
  BrainslugContext context;
  AsyncTask asyncTask;

  ExecuteTaskCallable(BrainslugContext context, AsyncTask asyncTask, AsyncTaskExecutor asyncTaskExecutor, RetryStrategy retryStrategy) {
    this.context = context;
    this.asyncTask = asyncTask;
    this.asyncTaskExecutor = asyncTaskExecutor;
    this.retryStrategy = retryStrategy;
  }

  @Override
  public AsyncTaskExecutionResult call() {
    if (asyncTask.getRetries() >= asyncTask.getMaxRetries()) {
      throw new IllegalStateException();
    }

    AsyncTaskExecutionResult execution = asyncTaskExecutor.execute(asyncTask, context);
    if (execution.isFailed()) {
      context.getAsyncTaskStore().storeTask(asyncTask
        .incrementRetries()
        .withDueDate(retryStrategy
            .nextRetry(asyncTask.getRetries(), getBaseDate()).getTime())
        .withErrorDetails(new AsyncTaskErrorDetails(execution.getException().get()))
        );
    }
    return execution;
  }

  public Date getBaseDate() {
    return new Date();
  }
}
