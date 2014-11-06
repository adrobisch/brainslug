package brainslug.flow.execution.async;

import brainslug.flow.node.task.RetryStrategy;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRetryStrategy implements RetryStrategy {

  public static AbstractRetryStrategy linear(final long intervalDuration, final TimeUnit intervalUnit) {
    return new AbstractRetryStrategy() {
      @Override
      public Date nextRetry(long retryCount, Date baseDate) {
        return new Date(baseDate.getTime() + intervalUnit.toMillis(intervalDuration));
      }

      @Override
      public String toString() {
        return String.format("linear retry, duration: %s, unit: %s", intervalDuration, intervalDuration);
      }
    };
  }

  public static AbstractRetryStrategy quadratic(final long intervalDuration, final TimeUnit intervalUnit) {
    return new AbstractRetryStrategy() {
      @Override
      public Date nextRetry(long retryCount, Date baseDate) {
        return new Date(baseDate.getTime() + intervalUnit.toMillis(intervalDuration * retryCount * retryCount));
      }

      @Override
      public String toString() {
        return String.format("quadratic retry, duration: %s, unit: %s", intervalDuration, intervalDuration);
      }
    };
  }

}
