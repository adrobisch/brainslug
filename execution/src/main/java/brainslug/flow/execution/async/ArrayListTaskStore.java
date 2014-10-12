package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.List;

public class ArrayListTaskStore implements AsyncTaskStore {
  List<AsyncTask> tasks = new ArrayList<AsyncTask>();

  @Override
  public AsyncTask storeTask(AsyncTask asyncTask) {
    if (tasks.contains(asyncTask)) {
      return asyncTask.incrementVersion();
    } else {
      tasks.add(asyncTask);
      return asyncTask;
    }
  }

  @Override
  public boolean removeTask(AsyncTask asyncTask) {
    return tasks.remove(asyncTask);
  }

  @Override
  public List<AsyncTask> getTasks(AsyncTaskQuery taskQuery) {
    return tasks.subList(0, (int) Math.min(taskQuery.maxCount, tasks.size()));
  }

  @Override
  public Option<AsyncTask> getTask(Identifier taskNodeId, Identifier instanceId, Identifier definitionId) {
    for (AsyncTask task : tasks) {
      if (task.getTaskNodeId().equals(taskNodeId) &&
          task.getInstanceId().equals(instanceId) &&
          task.getDefinitionId().equals(definitionId)) {
        return Option.of(task);
      }
    }
    return Option.empty();
  }

}
