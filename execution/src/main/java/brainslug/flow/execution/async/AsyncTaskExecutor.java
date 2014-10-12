package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.TriggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncTaskExecutor {
  private Logger log = LoggerFactory.getLogger(AsyncTaskExecutor.class);

  public AsyncTaskExecutionResult execute(AsyncTask asyncTask, BrainslugContext context) {
    log.debug("executing async task: {}", asyncTask);

    AsyncTaskExecutionResult result = trigger(asyncTask, context);

    if (!context.getAsyncTaskStore().removeTask(asyncTask)) {
      throw new IllegalStateException("unable to remove task " + asyncTask);
    }

    return result;
  }

  protected AsyncTaskExecutionResult trigger(AsyncTask asyncTask, BrainslugContext context) {
    try {
      context.trigger(
        new TriggerContext()
          .instanceId(asyncTask.getInstanceId())
          .definitionId(asyncTask.getDefinitionId())
          .nodeId(asyncTask.getTaskNodeId())
          .async(true)
      );
      return new AsyncTaskExecutionResult();
    } catch (Exception e) {
      return new AsyncTaskExecutionResult().setFailed(true).withException(e);
    }
  }

}
