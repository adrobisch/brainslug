package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.IdUtil;
import brainslug.util.Option;

import java.util.Date;

import static brainslug.util.IdUtil.id;

public class AsyncTask {
  Identifier id;
  Identifier taskNodeId;
  Identifier instanceId;
  Identifier definitionId;

  long createdDate = 0;
  long dueDate = 0;

  long retries = 0;
  long maxRetries = 5;

  long version = 0;

  AsyncTaskErrorDetails errorDetails;

  public AsyncTask() {
  }

  public AsyncTask(String id, String taskNodeId, String instanceId, String definitionId, Long createdDate, Long dueDate, Long retries, Long maxRetries, Long version) {
    this.id = id(id);
    this.taskNodeId = id(taskNodeId);
    this.instanceId = id(instanceId);
    this.definitionId = id(definitionId);
    this.createdDate = createdDate;
    this.dueDate = dueDate;
    this.retries = retries;
    this.maxRetries = maxRetries;
    this.version = version;
  }

  public Identifier getTaskNodeId() {
    return taskNodeId;
  }

  public AsyncTask withTaskNodeId(Identifier taskNodeId) {
    this.taskNodeId = taskNodeId;
    return this;
  }

  public Identifier getInstanceId() {
    return instanceId;
  }

  public AsyncTask withInstanceId(Identifier instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public Identifier getDefinitionId() {
    return definitionId;
  }

  public AsyncTask withDefinitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public long getDueDate() {
    return dueDate;
  }

  public AsyncTask withDueDate(long delay) {
    this.dueDate = delay;
    return this;
  }

  public long getRetries() {
    return retries;
  }

  public AsyncTask incrementRetries() {
    this.retries++;
    return this;
  }

  public long getMaxRetries() {
    return maxRetries;
  }

  public AsyncTask withMaxRetries(long maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public AsyncTask incrementVersion() {
    this.version++;
    return this;
  }

  public AsyncTask withVersion(long version) {
    this.version = version;
    return this;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public AsyncTask withCreatedDate(long createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Option<AsyncTaskErrorDetails> getErrorDetails() {
    return Option.of(errorDetails);
  }

  public AsyncTask withErrorDetails(AsyncTaskErrorDetails errorDetails) {
    this.errorDetails = errorDetails;
    return this;
  }

  public Option<Identifier> getId() {
    return Option.of(id);
  }

  public AsyncTask withId(Identifier id) {
    this.id = id;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AsyncTask asyncTask = (AsyncTask) o;

    if (dueDate != asyncTask.dueDate) return false;
    if (maxRetries != asyncTask.maxRetries) return false;
    if (retries != asyncTask.retries) return false;
    if (version != asyncTask.version) return false;
    if (definitionId != null ? !definitionId.equals(asyncTask.definitionId) : asyncTask.definitionId != null)
      return false;
    if (errorDetails != null ? !errorDetails.equals(asyncTask.errorDetails) : asyncTask.errorDetails != null)
      return false;
    if (instanceId != null ? !instanceId.equals(asyncTask.instanceId) : asyncTask.instanceId != null) return false;
    if (taskNodeId != null ? !taskNodeId.equals(asyncTask.taskNodeId) : asyncTask.taskNodeId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = taskNodeId != null ? taskNodeId.hashCode() : 0;
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    result = 31 * result + (definitionId != null ? definitionId.hashCode() : 0);
    result = 31 * result + (int) (dueDate ^ (dueDate >>> 32));
    result = 31 * result + (int) (retries ^ (retries >>> 32));
    result = 31 * result + (int) (maxRetries ^ (maxRetries >>> 32));
    result = 31 * result + (int) (version ^ (version >>> 32));
    result = 31 * result + (errorDetails != null ? errorDetails.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AsyncTask{" +
      "taskNodeId=" + taskNodeId +
      ", instanceId=" + instanceId +
      ", definitionId=" + definitionId +
      ", dueDate=" + dueDate +
      ", retries=" + retries +
      ", maxRetries=" + maxRetries +
      ", version=" + version +
      ", errorDetails=" + errorDetails +
      '}';
  }
}
