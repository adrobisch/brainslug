package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.execution.ExecutionContext;

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
