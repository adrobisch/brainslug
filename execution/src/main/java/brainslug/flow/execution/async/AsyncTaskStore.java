package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.Option;

import java.util.List;

public interface AsyncTaskStore {
  public AsyncTask storeTask(AsyncTask asyncTask);
  public boolean removeTask(AsyncTask asyncTask);
  public List<AsyncTask> getTasks(AsyncTaskQuery taskQuery);
  public Option<AsyncTask> getTask(Identifier taskNodeId, Identifier instanceId, Identifier definitionId);
}
