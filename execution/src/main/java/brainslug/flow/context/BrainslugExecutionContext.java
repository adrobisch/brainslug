package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceProperty;

public class BrainslugExecutionContext implements ExecutionContext {

  Registry registry;
  private FlowInstance flowInstance;
  TriggerContext trigger;

  public BrainslugExecutionContext(FlowInstance flowInstance, TriggerContext trigger, Registry registry) {
    this.flowInstance = flowInstance;
    this.trigger = trigger;
    this.registry = registry;
  }

  public TriggerContext getTrigger() {
    return trigger;
  }

  @Override
  public FlowInstance getInstance() {
    return flowInstance;
  }

  @Override
  public boolean isAsync() {
    return trigger.isAsync();
  }

  @Override
  public boolean isSignaling() {
    return trigger.isSignaling();
  }

  @Override
  public void setProperty(String key, Object value) {
    trigger.setProperty(key, value);
  }

  @Override
  public void setProperties(FlowInstanceProperties executionProperties) {
    trigger.setProperties(executionProperties);
  }

  @Override
  public FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties() {
    return trigger.getProperties();
  }

  @Override
  public <T> T property(Property<T> key, Class<T> clazz) {
    return property(key.getValue(), clazz);
  }

  @Override
  public <T> T property(Identifier key, Class<T> clazz) {
    return getTrigger().getProperty(key.stringValue(), clazz);
  }

  @Override
  public <T> T property(String key, Class<T> clazz) {
    return getTrigger().getProperty(key, clazz);
  }

  @Override
  public <T> T property(Enum key, Class<T> clazz) {
    return getTrigger().getProperty(key.name(), clazz);
  }

  @Override
  public <T> T service(Class<T> clazz) {
    return registry.getService(clazz);
  }
}
