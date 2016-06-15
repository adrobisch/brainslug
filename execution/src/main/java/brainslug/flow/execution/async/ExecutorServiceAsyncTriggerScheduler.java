package brainslug.flow.execution.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceAsyncTriggerScheduler extends AbstractAsyncTriggerScheduler {

  private Logger log = LoggerFactory.getLogger(ExecutorServiceAsyncTriggerScheduler.class);

  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  ExecutorService taskExecutorService = Executors.newCachedThreadPool();

  AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();

  @Override
  protected void internalStart() {
    log.info("starting async job scheduling with options: " + options);

    Runnable executeTasksRunnable = executeTasksRunnable();

    scheduledExecutorService.scheduleAtFixedRate(executeTasksRunnable,
      options.getScheduleDelay(),
      options.getSchedulePeriod(),
      options.getScheduleUnit()
    );
  }

  Runnable executeTasksRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        new FutureTask<List<Future<AsyncTriggerExecutionResult>>>(createExecutionCallable()).run();
      }
    };
  }

  ExecuteTasksCallable createExecutionCallable() {
    return new ExecuteTasksCallable(context, asyncTriggerStore, options, taskExecutorService, asyncTriggerExecutor);
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  public ExecutorServiceAsyncTriggerScheduler withScheduledExecutorService(ScheduledExecutorService executorService) {
    this.scheduledExecutorService = executorService;
    return this;
  }

  public ExecutorService getTaskExecutorService() {
    return taskExecutorService;
  }

  public ExecutorServiceAsyncTriggerScheduler withTaskExecutorService(ExecutorService taskExecutor) {
    this.taskExecutorService = taskExecutor;
    return this;
  }

  public AsyncTriggerExecutor getAsyncTriggerExecutor() {
    return asyncTriggerExecutor;
  }

  public ExecutorServiceAsyncTriggerScheduler withAsyncTriggerExecutor(AsyncTriggerExecutor asyncTriggerExecutor) {
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    return this;
  }

  @Override
  public void pollAndExecute() {
    executeTasksRunnable().run();
  }
}
