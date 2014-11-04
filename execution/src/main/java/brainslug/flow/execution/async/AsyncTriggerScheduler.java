package brainslug.flow.execution.async;

import brainslug.flow.context.ContextAware;

public interface AsyncTriggerScheduler extends ContextAware {
  public void start(AsyncTriggerSchedulerOptions taskSchedulerOptions);
  public void stop();
  public void schedule(AsyncTrigger asyncTrigger);
}
