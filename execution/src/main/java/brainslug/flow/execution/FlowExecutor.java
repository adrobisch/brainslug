package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.TriggerContext;

public interface FlowExecutor {
  Identifier startFlow(TriggerContext trigger);
  void trigger(TriggerContext trigger);
}
