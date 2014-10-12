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
    };
  }

  public static AbstractRetryStrategy quadratic(final long intervalDuration, final TimeUnit intervalUnit) {
    return new AbstractRetryStrategy() {
      @Override
      public Date nextRetry(long retryCount, Date baseDate) {
        return new Date(baseDate.getTime() + intervalUnit.toMillis(intervalDuration * retryCount * retryCount));
      }
    };
  }

}
