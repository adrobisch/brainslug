package brainslug.flow.execution;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.event.TriggerEvent;

public interface ExecutionContext {
  BrainslugContext getBrainslugContext();
  TriggerEvent getTrigger();
}
