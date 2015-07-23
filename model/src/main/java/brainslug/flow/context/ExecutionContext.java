package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.flow.instance.FlowInstance;

public interface ExecutionContext {
  TriggerContext getTrigger();
  FlowInstance getInstance();
  <T> T property(Property<T> key, Class<T> clazz);
  <T> T property(Identifier key, Class<T> clazz);
  <T> T property(String key, Class<T> clazz);
  <T> T property(Enum key, Class<T> clazz);
  <T> T service(Class<T> clazz);
}
