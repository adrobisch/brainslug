package brainslug.jdbc.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Blob;

@Entity
@Table(name = "ASYNC_TASK_ERROR_DETAILS")
public class AsyncTaskErrorDetailsEntity {
  @Id
  @Column(name = "ID")
  protected String id;

  @NotNull
  @Column(name = "CREATED")
  protected Long created;

  @Version
  @Column(name = "VERSION")
  protected Long version;

  @NotNull
  @Column(name = "STACK_TRACE")
  protected byte[] stackTrace;

  @NotNull
  @Column(name = "EXCEPTION_TYPE")
  protected String exceptionType;

  @NotNull
  @Column(name = "MESSAGE")
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
    return stackTrace;
  }

  public AsyncTaskErrorDetailsEntity withStackTrace(byte[] stackTrace) {
    this.stackTrace = stackTrace;
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
