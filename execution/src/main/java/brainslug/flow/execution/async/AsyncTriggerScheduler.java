package brainslug.flow.execution.async;

import brainslug.flow.context.BrainslugContext;

public interface AsyncTriggerScheduler {
  void start(BrainslugContext brainslugContext, AsyncTriggerStore asyncTriggerStore, AsyncTriggerSchedulerOptions taskSchedulerOptions);
  void stop();
  void schedule(AsyncTrigger asyncTrigger);
  void pollAndExecute();
}
