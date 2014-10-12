package brainslug.flow.execution.async;

import brainslug.flow.context.ContextAware;

public interface AsyncTaskScheduler extends ContextAware {
  public void start(AsyncTaskSchedulerOptions taskSchedulerOptions);
  public void stop();
  public void scheduleTask(AsyncTask asyncTask);
}
