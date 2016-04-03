package brainslug.flow.execution;

import brainslug.flow.context.TriggerContext;
import brainslug.flow.execution.instance.FlowInstance;

public interface FlowExecutor {
  FlowInstance startFlow(TriggerContext trigger);
  void trigger(TriggerContext trigger);
}
