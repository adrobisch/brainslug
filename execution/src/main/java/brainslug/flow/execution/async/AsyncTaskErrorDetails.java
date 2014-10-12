package brainslug.flow.execution.async;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AsyncTaskErrorDetails {
  Exception exception;

  public AsyncTaskErrorDetails(Exception exception) {
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

  public AsyncTaskErrorDetails withException(Exception exception) {
    this.exception = exception;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AsyncTaskErrorDetails that = (AsyncTaskErrorDetails) o;

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
