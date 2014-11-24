package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.Trigger;

import brainslug.flow.node.task.RetryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class AsyncTriggerExecutor {
  private Logger log = LoggerFactory.getLogger(AsyncTriggerExecutor.class);

  public AsyncTriggerExecutionResult execute(AsyncTrigger asyncTrigger, RetryStrategy retryStrategy, BrainslugContext context, AsyncTriggerStore asyncTriggerStore) {
    log.debug("executing async task: {}", asyncTrigger);

    AsyncTriggerExecutionResult execution = trigger(asyncTrigger, context);

    if (execution.isFailed()) {
      setErrorDetailsAndRetry(asyncTrigger, execution, retryStrategy, asyncTriggerStore);
    } else {
      removeTrigger(asyncTrigger, asyncTriggerStore);
    }

    return execution;
  }

  protected void setErrorDetailsAndRetry(AsyncTrigger asyncTrigger, AsyncTriggerExecutionResult execution, RetryStrategy retryStrategy, AsyncTriggerStore asyncTriggerStore) {
    asyncTriggerStore.updateTrigger(asyncTrigger
        .incrementRetries()
        .withDueDate(retryStrategy
          .nextRetry(asyncTrigger.getRetries(), getBaseDate()).getTime())
        .withErrorDetails(new AsyncTriggerErrorDetails(execution.getException().get()))
    );
  }

  protected void removeTrigger(AsyncTrigger asyncTrigger, AsyncTriggerStore asyncTriggerStore) {
    if (!asyncTriggerStore.removeTrigger(asyncTrigger)) {
      throw new IllegalStateException("unable to remove task " + asyncTrigger);
    }
  }

  protected AsyncTriggerExecutionResult trigger(AsyncTrigger asyncTrigger, BrainslugContext context) {
    try {
      context.trigger(
        new Trigger()
          .instanceId(asyncTrigger.getInstanceId())
          .definitionId(asyncTrigger.getDefinitionId())
          .nodeId(asyncTrigger.getNodeId())
          .async(true)
          .signaling(true)
      );
      return new AsyncTriggerExecutionResult();
    } catch (Exception e) {
      return new AsyncTriggerExecutionResult().setFailed(true).withException(e);
    }
  }

  public Date getBaseDate() {
    return new Date();
  }

}
