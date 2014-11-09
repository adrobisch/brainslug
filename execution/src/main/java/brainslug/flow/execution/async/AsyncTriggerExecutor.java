package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.context.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncTriggerExecutor {
  private Logger log = LoggerFactory.getLogger(AsyncTriggerExecutor.class);

  public AsyncTriggerExecutionResult execute(AsyncTrigger asyncTrigger, DefaultBrainslugContext context) {
    log.debug("executing async task: {}", asyncTrigger);

    AsyncTriggerExecutionResult result = trigger(asyncTrigger, context);

    if (!context.getAsyncTriggerStore().removeTrigger(asyncTrigger)) {
      throw new IllegalStateException("unable to remove task " + asyncTrigger);
    }

    return result;
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

}
