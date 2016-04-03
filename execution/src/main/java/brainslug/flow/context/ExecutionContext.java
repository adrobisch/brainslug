package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.flow.execution.instance.FlowInstance;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import brainslug.flow.execution.instance.FlowInstanceProperty;

public interface ExecutionContext {
  FlowInstance getInstance();

  boolean isAsync();
  boolean isSignaling();

  void setProperty(String key, Object value);
  <T> void setProperty(Property<T> property, T value);
  void setProperty(Identifier key, Object value);
  void setProperty(Enum key, Object value);

  void setProperty(String key, Object value, boolean isTransient);
  <T> void setProperty(Property<T> property, T value, boolean isTransient);
  void setProperty(Identifier key, Object value, boolean isTransient);
  void setProperty(Enum key, Object value, boolean isTransient);

  void setProperties(FlowInstanceProperties executionProperties);
  FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties();

  <T> T property(Property<T> property);
  <T> T property(Identifier id, Class<T> clazz);
  <T> T property(String key, Class<T> clazz);
  <T> T property(Enum key, Class<T> clazz);
  <T> T service(Class<T> clazz);
  Object service(String className);
}
