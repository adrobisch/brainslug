package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecuteTasksCallable implements Callable<List<Future<AsyncTriggerExecutionResult>>> {
  private AsyncTriggerExecutor asyncTriggerExecutor;
  private Logger log = LoggerFactory.getLogger(ExecuteTasksCallable.class);

  BrainslugContext context;
  AsyncTriggerSchedulerOptions options;
  ExecutorService taskExecutorService;

  ExecuteTasksCallable(BrainslugContext context,
                       AsyncTriggerSchedulerOptions options,
                       ExecutorService taskExecutorService,
                       AsyncTriggerExecutor asyncTriggerExecutor) {
    this.context = context;
    this.options = options;
    this.taskExecutorService = taskExecutorService;
    this.asyncTriggerExecutor = asyncTriggerExecutor;
  }

  @Override
  public synchronized List<Future<AsyncTriggerExecutionResult>> call() {
    try {
      List<ExecuteTaskCallable> tasksToBeExecuted = getTasksToBeExecuted();
      log.debug("scheduled triggers for execution: " + tasksToBeExecuted);
      return taskExecutorService.invokeAll(tasksToBeExecuted);
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("task execution was interrupted", e);
      }
      log.error("unable to execute triggers", e);
      throw new RuntimeException("unable to execute triggers: ", e);
    }
  }

  protected List<ExecuteTaskCallable> getTasksToBeExecuted() {
    List<ExecuteTaskCallable> tasksToBeExecuted = new ArrayList<ExecuteTaskCallable>();

    for (AsyncTrigger task: getTasksToTrigger()) {
      AsyncTrigger updatedTask = context.getAsyncTriggerStore().storeTrigger(task);

      TaskDefinition taskDefinition = context.getDefinitionStore()
        .findById(updatedTask.getDefinitionId())
        .getNode(updatedTask.getNodeId(), TaskDefinition.class);

      tasksToBeExecuted.add(new ExecuteTaskCallable(context, updatedTask, asyncTriggerExecutor, taskDefinition
        .getRetryStrategy()
        .orElse(AbstractRetryStrategy.quadratic(30, TimeUnit.SECONDS))
      ));
    }
    return tasksToBeExecuted;
  }

  protected List<AsyncTrigger> getTasksToTrigger() {
    return context.getAsyncTriggerStore().getTriggers(
      new AsyncTriggerQuery()
        .withMaxCount(options.getMaxTaskCount())
        .withOverdueDate(new Date())
    );
  }

}
