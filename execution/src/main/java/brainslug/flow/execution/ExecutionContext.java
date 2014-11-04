package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;

public interface ExecutionContext {
  BrainslugContext getBrainslugContext();
  TriggerContext<?> getTrigger();
  <T> T property(Identifier key, Class<T> clazz);
  <T> T property(Class<T> clazz);
  <T> T service(Class<T> clazz);
}
