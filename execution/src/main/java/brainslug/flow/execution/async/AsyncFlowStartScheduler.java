package brainslug.flow.execution.async;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.context.BrainslugContext;

import java.util.Collection;

public interface AsyncFlowStartScheduler {
  public void start(SchedulerOptions schedulerOptions, BrainslugContext brainslugContext, Collection<FlowDefinition> definitions);
  public void stop();
}
