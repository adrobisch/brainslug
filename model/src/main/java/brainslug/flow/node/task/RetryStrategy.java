package brainslug.flow.node.task;

import java.util.Date;

public interface RetryStrategy {
  Date nextRetry(long retryCount, Date baseDate);
}
