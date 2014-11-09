package brainslug.flow.execution.async;

import brainslug.flow.context.DefaultBrainslugContext;

public interface AsyncTriggerScheduler {
  public void start(DefaultBrainslugContext brainslugContext, AsyncTriggerSchedulerOptions taskSchedulerOptions);
  public void stop();
  public void schedule(AsyncTrigger asyncTrigger);
}
