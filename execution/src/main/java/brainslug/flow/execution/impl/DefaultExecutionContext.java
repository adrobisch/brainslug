package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.event.TriggerEvent;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.Identifier;

public class DefaultExecutionContext implements ExecutionContext {

  TriggerEvent trigger;
  BrainslugContext brainslugContext;

  public DefaultExecutionContext(TriggerEvent trigger, BrainslugContext brainslugContext) {
    this.trigger = trigger;
    this.brainslugContext = brainslugContext;
  }

  public TriggerEvent getTrigger() {
    return trigger;
  }

  public BrainslugContext getBrainslugContext() {
    return brainslugContext;
  }
}
