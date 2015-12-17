package brainslug.flow.execution.node;

import brainslug.flow.context.TriggerContext;

public class FlowNodeExecutionException extends RuntimeException{
  TriggerContext triggerContext;

  public FlowNodeExecutionException() {
    super();
  }

  public FlowNodeExecutionException(String message) {
    super(message);
  }

  public FlowNodeExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public FlowNodeExecutionException(Throwable cause) {
    super(cause);
  }

  public TriggerContext getTriggerContext() {
    return triggerContext;
  }

  public FlowNodeExecutionException setTriggerContext(TriggerContext triggerContext) {
    this.triggerContext = triggerContext;
    return this;
  }
}
