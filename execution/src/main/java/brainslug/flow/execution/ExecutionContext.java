package brainslug.flow.execution;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.listener.TriggerContext;

public interface ExecutionContext {
  BrainslugContext getBrainslugContext();
  TriggerContext getTrigger();
}
