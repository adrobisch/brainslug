package brainslug.flow.execution.async;

import brainslug.flow.FlowDefinition;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.DefinitionStore;

import java.util.Collection;

public interface AsyncFlowStartScheduler {
  public void start(SchedulerOptions schedulerOptions, BrainslugContext brainslugContext, Collection<FlowDefinition> definitionStore);
  public void stop();
}
