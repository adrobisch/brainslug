package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.Option;

import java.util.List;

public interface AsyncTriggerStore {
  public AsyncTrigger storeTrigger(AsyncTrigger asyncTrigger);
  public boolean removeTrigger(AsyncTrigger asyncTrigger);
  public List<AsyncTrigger> getTriggers(AsyncTriggerQuery taskQuery);
  public Option<AsyncTrigger> getTrigger(Identifier taskNodeId, Identifier instanceId, Identifier definitionId);
}
