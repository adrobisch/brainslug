package brainslug.flow.execution.async;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AsyncTriggerErrorDetails {
  Exception exception;

  public AsyncTriggerErrorDetails(Exception exception) {
    this.exception = exception;
  }

  public String getStackTrace() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    return sw.toString();
  }


  public Exception getException() {
    return exception;
  }

  public AsyncTriggerErrorDetails withException(Exception exception) {
    this.exception = exception;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AsyncTriggerErrorDetails that = (AsyncTriggerErrorDetails) o;

    if (exception != null ? !exception.equals(that.exception) : that.exception != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return exception != null ? exception.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "AsyncTaskErrorDetails{" +
      "exception=" + exception +
      '}';
  }
}
