package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.Registry;
import brainslug.flow.context.TriggerContext;

public class DefaultExecutionContext implements ExecutionContext {

  Registry registry;
  TriggerContext trigger;

  public DefaultExecutionContext(TriggerContext trigger, Registry registry) {
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
  public <T> T property(Class<T> clazz) {
    return getTrigger().getProperty(clazz);
  }

  @Override
  public <T> T service(Class<T> clazz) {
    return registry.getService(clazz);
  }
}
