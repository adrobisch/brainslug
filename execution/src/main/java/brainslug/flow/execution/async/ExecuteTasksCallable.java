package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.TaskDefinition;
import brainslug.flow.node.task.RetryStrategy;
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
    log.info("executing async triggers");
    try {
      List<ExecuteTaskCallable> tasksToBeExecuted = getTasksToBeExecuted();
      log.debug(String.format("scheduled %d trigger(s) for execution: %s", tasksToBeExecuted.size(), tasksToBeExecuted));
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

    for (AsyncTrigger trigger: getTasksToTrigger()) {
      AsyncTrigger updatedTrigger = context.getAsyncTriggerStore().storeTrigger(trigger);

      FlowNodeDefinition nodeDefinition = context.getDefinitionStore()
        .findById(updatedTrigger.getDefinitionId())
        .getNode(updatedTrigger.getNodeId(), FlowNodeDefinition.class);

      RetryStrategy retryStrategy = getRetryStrategy(nodeDefinition);

      tasksToBeExecuted.add(new ExecuteTaskCallable(context, updatedTrigger, asyncTriggerExecutor, retryStrategy));
    }
    return tasksToBeExecuted;
  }

  protected RetryStrategy getRetryStrategy(FlowNodeDefinition nodeDefinition) {
    if (nodeDefinition instanceof TaskDefinition) {
      return ((TaskDefinition) nodeDefinition).getRetryStrategy()
        .orElse(AbstractRetryStrategy.quadratic(30, TimeUnit.SECONDS));
    }
    return AbstractRetryStrategy.linear(60, TimeUnit.SECONDS);
  }

  protected List<AsyncTrigger> getTasksToTrigger() {
    return context.getAsyncTriggerStore().getTriggers(
      new AsyncTriggerQuery()
        .withMaxCount(options.getMaxTaskCount())
        .withOverdueDate(new Date())
    );
  }

}
