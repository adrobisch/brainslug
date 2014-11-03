package brainslug.flow.execution;

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

  public BrainslugContext getBrainslugContext() {
    return brainslugContext;
  }
}
