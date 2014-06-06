package brainslug.flow.execution;

import brainslug.flow.context.BrainslugContext;

public interface ExecutionContext {
  BrainslugContext getBrainslugContext();
  TriggerContext getTrigger();
}
