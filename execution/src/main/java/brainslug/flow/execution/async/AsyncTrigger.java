package brainslug.flow.execution.async;

import brainslug.flow.Identifier;
import brainslug.util.Option;

import static brainslug.util.IdUtil.id;

public class AsyncTrigger {
  Identifier id;
  Identifier nodeId;
  Identifier instanceId;
  Identifier definitionId;

  long createdDate = 0;
  long dueDate = 0;

  long retries = 0;
  long maxRetries = 5;

  long version = 0;

  AsyncTriggerErrorDetails errorDetails;

  public AsyncTrigger() {
  }

  public AsyncTrigger(String id, String nodeId, String instanceId, String definitionId, Long createdDate, Long dueDate, Long retries, Long maxRetries, Long version) {
    this.id = id(id);
    this.nodeId = id(nodeId);
    this.instanceId = id(instanceId);
    this.definitionId = id(definitionId);
    this.createdDate = createdDate;
    this.dueDate = dueDate;
    this.retries = retries;
    this.maxRetries = maxRetries;
    this.version = version;
  }

  public Identifier getNodeId() {
    return nodeId;
  }

  public AsyncTrigger withNodeId(Identifier taskNodeId) {
    this.nodeId = taskNodeId;
    return this;
  }

  public Identifier getInstanceId() {
    return instanceId;
  }

  public AsyncTrigger withInstanceId(Identifier instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public Identifier getDefinitionId() {
    return definitionId;
  }

  public AsyncTrigger withDefinitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public long getDueDate() {
    return dueDate;
  }

  public AsyncTrigger withDueDate(long dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  public long getRetries() {
    return retries;
  }

  public AsyncTrigger incrementRetries() {
    this.retries++;
    return this;
  }

  public long getMaxRetries() {
    return maxRetries;
  }

  public AsyncTrigger withMaxRetries(long maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public AsyncTrigger incrementVersion() {
    this.version++;
    return this;
  }

  public AsyncTrigger withVersion(long version) {
    this.version = version;
    return this;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public AsyncTrigger withCreatedDate(long createdDate) {
    this.createdDate = createdDate;
    return this;
  }

  public Option<AsyncTriggerErrorDetails> getErrorDetails() {
    return Option.of(errorDetails);
  }

  public AsyncTrigger withErrorDetails(AsyncTriggerErrorDetails errorDetails) {
    this.errorDetails = errorDetails;
    return this;
  }

  public Option<Identifier> getId() {
    return Option.of(id);
  }

  public AsyncTrigger withId(Identifier id) {
    this.id = id;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AsyncTrigger asyncTrigger = (AsyncTrigger) o;

    if (dueDate != asyncTrigger.dueDate) return false;
    if (maxRetries != asyncTrigger.maxRetries) return false;
    if (retries != asyncTrigger.retries) return false;
    if (version != asyncTrigger.version) return false;
    if (definitionId != null ? !definitionId.equals(asyncTrigger.definitionId) : asyncTrigger.definitionId != null)
      return false;
    if (errorDetails != null ? !errorDetails.equals(asyncTrigger.errorDetails) : asyncTrigger.errorDetails != null)
      return false;
    if (instanceId != null ? !instanceId.equals(asyncTrigger.instanceId) : asyncTrigger.instanceId != null) return false;
    if (nodeId != null ? !nodeId.equals(asyncTrigger.nodeId) : asyncTrigger.nodeId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = nodeId != null ? nodeId.hashCode() : 0;
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
    return "AsyncTrigger{" +
      "nodeId=" + nodeId +
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
