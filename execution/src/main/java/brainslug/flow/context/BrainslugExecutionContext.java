package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.flow.instance.FlowInstance;

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
