package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecuteTasksCallable implements Callable<List<Future<AsyncTaskExecutionResult>>> {
  private AsyncTaskExecutor asyncTaskExecutor;
  private Logger log = LoggerFactory.getLogger(ExecuteTasksCallable.class);

  BrainslugContext context;
  AsyncTaskSchedulerOptions options;
  ExecutorService taskExecutorService;

  ExecuteTasksCallable(BrainslugContext context,
                       AsyncTaskSchedulerOptions options,
                       ExecutorService taskExecutorService,
                       AsyncTaskExecutor asyncTaskExecutor) {
    this.context = context;
    this.options = options;
    this.taskExecutorService = taskExecutorService;
    this.asyncTaskExecutor = asyncTaskExecutor;
  }

  @Override
  public synchronized List<Future<AsyncTaskExecutionResult>> call() {
    try {
      List<ExecuteTaskCallable> tasksToBeExecuted = getTasksToBeExecuted();
      log.debug("scheduled tasks for execution: " + tasksToBeExecuted);
      return taskExecutorService.invokeAll(tasksToBeExecuted);
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("task execution was interrupted", e);
      }
      log.error("unable to execute tasks", e);
      throw new RuntimeException("unable to execute tasks: ", e);
    }
  }

  protected List<ExecuteTaskCallable> getTasksToBeExecuted() {
    List<ExecuteTaskCallable> tasksToBeExecuted = new ArrayList<ExecuteTaskCallable>();

    for (AsyncTask task: getTasksToTrigger()) {
      AsyncTask updatedTask = context.getAsyncTaskStore().storeTask(task);

      TaskDefinition taskDefinition = context.getDefinitionStore()
        .findById(updatedTask.getDefinitionId())
        .getNode(updatedTask.getTaskNodeId(), TaskDefinition.class);

      tasksToBeExecuted.add(new ExecuteTaskCallable(context, updatedTask, asyncTaskExecutor, taskDefinition
        .getRetryStrategy()
        .orElse(AbstractRetryStrategy.quadratic(30, TimeUnit.SECONDS))
      ));
    }
    return tasksToBeExecuted;
  }

  protected List<AsyncTask> getTasksToTrigger() {
    return context.getAsyncTaskStore().getTasks(
      new AsyncTaskQuery()
        .withMaxCount(options.getMaxTaskCount())
    );
  }

}
