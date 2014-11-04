package brainslug.flow.execution.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceScheduler extends AbstractAsyncTriggerScheduler {

  private Logger log = LoggerFactory.getLogger(ExecutorServiceScheduler.class);

  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  ExecutorService taskExecutorService = Executors.newCachedThreadPool();

  AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();

  @Override
  protected void internalStart() {
    log.info("starting async job scheduling with options: " + options);

    FutureTask<List<Future<AsyncTriggerExecutionResult>>> executeTasks =
      new FutureTask<List<Future<AsyncTriggerExecutionResult>>>(new ExecuteTasksCallable(context, options, taskExecutorService, asyncTriggerExecutor));

    scheduledExecutorService.scheduleAtFixedRate(executeTasks,
      options.getScheduleDelay(),
      options.getSchedulePeriod(),
      options.getScheduleUnit()
    );
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    return scheduledExecutorService;
  }

  public ExecutorServiceScheduler withScheduledExecutorService(ScheduledExecutorService executorService) {
    this.scheduledExecutorService = executorService;
    return this;
  }

  public ExecutorService getTaskExecutorService() {
    return taskExecutorService;
  }

  public ExecutorServiceScheduler withTaskExecutorService(ExecutorService taskExecutor) {
    this.taskExecutorService = taskExecutor;
    return this;
  }

  public AsyncTriggerExecutor getAsyncTriggerExecutor() {
    return asyncTriggerExecutor;
  }

  public ExecutorServiceScheduler withAsyncTriggerExecutor(AsyncTriggerExecutor asyncTriggerExecutor) {
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    return this;
  }
}
