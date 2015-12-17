package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceProperty;

public interface ExecutionContext {
  FlowInstance getInstance();

  boolean isAsync();
  boolean isSignaling();

  void setProperty(String key, Object value);
  <T> void setProperty(Property<T> property, T value);
  void setProperty(Identifier key, Object value);
  void setProperty(Enum key, Object value);

  void setProperties(FlowInstanceProperties executionProperties);
  FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties();

  <T> T property(Property<T> property);
  <T> T property(Identifier id, Class<T> clazz);
  <T> T property(String key, Class<T> clazz);
  <T> T property(Enum key, Class<T> clazz);
  <T> T service(Class<T> clazz);
  Object service(String className);
}
