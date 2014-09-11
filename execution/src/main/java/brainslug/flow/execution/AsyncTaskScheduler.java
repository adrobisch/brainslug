package brainslug.flow.execution;

import brainslug.flow.context.ContextAware;
import brainslug.flow.model.Identifier;

public interface AsyncTaskScheduler extends ContextAware {
  public void scheduleTask(Identifier taskNodeId, Identifier sourceNodeId, Identifier instanceId, Identifier definitionId);
  public void start();
  public void stop();
}
