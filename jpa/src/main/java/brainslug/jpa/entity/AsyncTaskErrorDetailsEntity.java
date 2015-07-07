package brainslug.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BS_ASYNC_TASK_ERROR_DETAILS")
public class AsyncTaskErrorDetailsEntity {
  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @Lob
  @NotNull
  @Column(name = "_STACK_TRACE")
  protected String stackTrace;

  @NotNull
  @Column(name = "_EXCEPTION_TYPE")
  protected String exceptionType;

  @NotNull
  @Column(name = "_MESSAGE")
  protected String message;


  public String getId() {
    return id;
  }

  public AsyncTaskErrorDetailsEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public AsyncTaskErrorDetailsEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public AsyncTaskErrorDetailsEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  public byte[] getStackTrace() {
    return stackTrace.getBytes();
  }

  public AsyncTaskErrorDetailsEntity withStackTrace(byte[] stackTrace) {
    this.stackTrace = new String(stackTrace);
    return this;
  }

  public String getExceptionType() {
    return exceptionType;
  }

  public AsyncTaskErrorDetailsEntity withExceptionType(String exceptionType) {
    this.exceptionType = exceptionType;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public AsyncTaskErrorDetailsEntity withMessage(String message) {
    this.message = message;
    return this;
  }
}
