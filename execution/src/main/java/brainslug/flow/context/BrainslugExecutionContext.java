package brainslug.flow.context;

import brainslug.flow.definition.Identifier;

public class BrainslugExecutionContext implements ExecutionContext {

  Registry registry;
  TriggerContext trigger;

  public BrainslugExecutionContext(TriggerContext trigger, Registry registry) {
    this.trigger = trigger;
    this.registry = registry;
  }

  public TriggerContext getTrigger() {
    return trigger;
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
