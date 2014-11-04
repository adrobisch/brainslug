package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;

public class DefaultExecutionContext implements ExecutionContext {

  TriggerContext trigger;
  BrainslugContext brainslugContext;

  public DefaultExecutionContext(TriggerContext trigger, BrainslugContext brainslugContext) {
    this.trigger = trigger;
    this.brainslugContext = brainslugContext;
  }

  public TriggerContext<?> getTrigger() {
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
    return getBrainslugContext().getRegistry().getService(clazz);
  }

  // direct access should not be need
  @Deprecated
  public BrainslugContext getBrainslugContext() {
    return brainslugContext;
  }
}
