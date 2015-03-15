package brainslug.flow.context;

import brainslug.flow.Identifier;

public interface ExecutionContext {
  TriggerContext getTrigger();
  <T> T property(Identifier key, Class<T> clazz);
  <T> T property(String key, Class<T> clazz);
  <T> T property(Enum key, Class<T> clazz);
  <T> T property(Class<T> clazz);
  <T> T service(Class<T> clazz);
}
