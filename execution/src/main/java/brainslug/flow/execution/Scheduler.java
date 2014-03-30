package brainslug.flow.execution;

import brainslug.flow.context.ContextAware;
import brainslug.flow.model.Identifier;

public interface Scheduler extends ContextAware {
  public void scheduleTask(Identifier taskNodeId, Identifier definitionId);
}
