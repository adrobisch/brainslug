package brainslug.flow.execution;

import brainslug.flow.Identifier;

public interface FlowExecutor {
  Identifier startFlow(TriggerContext<?> trigger);
  void trigger(TriggerContext<?> triggerContext);
}
