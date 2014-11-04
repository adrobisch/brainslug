package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArrayListTriggerStore implements AsyncTriggerStore {
  List<AsyncTrigger> triggers = new ArrayList<AsyncTrigger>();

  @Override
  public AsyncTrigger storeTrigger(AsyncTrigger asyncTrigger) {
    if (triggers.contains(asyncTrigger)) {
      return asyncTrigger.incrementVersion();
    } else {
      triggers.add(asyncTrigger);
      return asyncTrigger;
    }
  }

  @Override
  public boolean removeTrigger(AsyncTrigger asyncTrigger) {
    return triggers.remove(asyncTrigger);
  }

  @Override
  public List<AsyncTrigger> getTriggers(AsyncTriggerQuery triggerQuery) {
    Date dueDate = triggerQuery.getOverdueDate().orElse(new Date());
    List<AsyncTrigger> overdueTriggers = getOverdueTriggers(dueDate);
    return overdueTriggers.subList(0, (int) Math.min(triggerQuery.maxCount, overdueTriggers.size()));
  }

  protected List<AsyncTrigger> getOverdueTriggers(Date dueDate) {
    List<AsyncTrigger> overdueTasks = new ArrayList<AsyncTrigger>();
    for (AsyncTrigger task: triggers) {
      if (dueDate.getTime() >= task.getDueDate()) {
        overdueTasks.add(task);
      }
    }
    return overdueTasks;
  }

  @Override
  public Option<AsyncTrigger> getTrigger(Identifier taskNodeId, Identifier instanceId, Identifier definitionId) {
    for (AsyncTrigger trigger: triggers) {
      if (trigger.getNodeId().equals(taskNodeId) &&
          trigger.getInstanceId().equals(instanceId) &&
          trigger.getDefinitionId().equals(definitionId)) {
        return Option.of(trigger);
      }
    }
    return Option.empty();
  }

}
