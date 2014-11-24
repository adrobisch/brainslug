package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;

public interface AsyncTriggerScheduler {
  public void start(BrainslugContext brainslugContext, AsyncTriggerStore asyncTriggerStore, AsyncTriggerSchedulerOptions taskSchedulerOptions);
  public void stop();
  public void schedule(AsyncTrigger asyncTrigger);
}
