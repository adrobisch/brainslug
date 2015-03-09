package brainslug.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BS_ASYNC_TASK")
public class AsyncTaskEntity {
  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @NotNull
  @Column(name = "_DEFINITION_ID")
  protected String definitionId;

  @NotNull
  @Column(name = "_DUE_DATE")
  protected Long dueDate;

  @NotNull
  @Column(name = "_INSTANCE_ID")
  protected String instanceId;

  @NotNull
  @Column(name = "_MAX_RETRIES")
  protected Long maxRetries;

  @NotNull
  @Column(name = "_RETRIES")
  protected Long retries;

  @NotNull
  @Column(name = "_TASK_NODE_ID")
  protected String taskNodeId;

  @OneToOne
  @JoinColumn(name = "_ERROR_DETAILS_ID")
  protected AsyncTaskErrorDetailsEntity errorDetails;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  public String getId() {
    return id;
  }

  public AsyncTaskEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public AsyncTaskEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public String getDefinitionId() {
    return definitionId;
  }

  public AsyncTaskEntity withDefinitionId(String definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public Long getDueDate() {
    return dueDate;
  }

  public AsyncTaskEntity withDueDate(Long dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public AsyncTaskEntity withInstanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public Long getMaxRetries() {
    return maxRetries;
  }

  public AsyncTaskEntity withMaxRetries(Long maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  public Long getRetries() {
    return retries;
  }

  public AsyncTaskEntity withRetries(Long retries) {
    this.retries = retries;
    return this;
  }

  public String getTaskNodeId() {
    return taskNodeId;
  }

  public AsyncTaskEntity withTaskNodeId(String taskNodeId) {
    this.taskNodeId = taskNodeId;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public AsyncTaskEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  public AsyncTaskErrorDetailsEntity getErrorDetails() {
    return errorDetails;
  }

  public AsyncTaskEntity withErrorDetails(AsyncTaskErrorDetailsEntity errorDetails) {
    this.errorDetails = errorDetails;
    return this;
  }

}
