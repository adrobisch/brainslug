package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.DefinitionStore;

public interface AsyncFlowStartScheduler {
  public void start(SchedulerOptions schedulerOptions, BrainslugContext brainslugContext, DefinitionStore definitionStore);
  public void stop();
}
